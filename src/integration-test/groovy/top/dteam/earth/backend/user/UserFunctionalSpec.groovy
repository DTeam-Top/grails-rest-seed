package top.dteam.earth.backend.user

import grails.core.GrailsApplication
import grails.gorm.transactions.Rollback
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification
import top.dteam.earth.backend.operation.SmsLog
import top.dteam.earth.backend.utils.TestUtils

@Integration
@Rollback
class UserFunctionalSpec extends Specification {

    GrailsApplication grailsApplication
    UserService userService

    void setup() {
        TestUtils.initEnv()
    }

    void cleanup() {
        TestUtils.clearEnv()
    }

    void "password在持久化后需要加密"() {
        setup:
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()

        when: 'insert'
        User user = new User(username: 13572211111, password: '123456', displayName: 'user')
                .save(flush: true)

        then:
        user.password != '123456'
        encoder.matches('123456', user.password)

        when: 'update'
        user.password = 'abcdef'
        user.save(flush: true)

        then:
        user.password != 'abcdef'
        encoder.matches('abcdef', user.password)
    }

    void "登录之后的响应应该包含用户的Displayname"() {
        when:
        String loginEndpointUrl = grailsApplication.config.grails.plugin.springsecurity.rest.login.endpointUrl
        String loginUrl = "http://localhost:${serverPort}${loginEndpointUrl}"
        RestBuilder restBuilder = new RestBuilder()
        RestResponse response = restBuilder.post(loginUrl) {
            json {
                username = TestUtils.admin
                password = TestUtils.admin
            }
        }

        then:
        response.status == 200
        response.json.displayName
    }

    void '冻结用户无法登录'() {
        setup:
        RestBuilder restBuilder = new RestBuilder()
        User user
        User.withNewTransaction {
            user = userService.createUserWithRole(
                    new User(username: '13500000001', password: 'test', displayName: 'user', enabled: false)
                    , 'ROLE_ADMIN')
        }

        when:
        String loginEndpointUrl = grailsApplication.config.grails.plugin.springsecurity.rest.login.endpointUrl
        RestResponse response = restBuilder.post("http://localhost:${serverPort}${loginEndpointUrl}") {
            json {
                username = user.username
                password = 'test'
            }
        }

        then:
        response.status == 401
    }

    void '登录成功的用户应该有登录记录'() {
        when:
        TestUtils.login(serverPort, TestUtils.admin, TestUtils.admin)
        String jwt = TestUtils.login(serverPort, TestUtils.admin, TestUtils.admin)
        try {
            TestUtils.login(serverPort, TestUtils.admin, 'wrongPassword')
        } catch (RuntimeException e) {
            // 登录失败用户不应该记录日志
        }

        // 登录成功之后正常访问不应该有登录日志
        new RestBuilder().get("http://localhost:${serverPort}/") {
            header('Authorization', "Bearer ${jwt}")
        }

        then:
        LoginHistory.count() == 2
    }

    void "登录过的用户应该可以refresh JWT"() {
        setup:
        RestBuilder restBuilder = new RestBuilder()
        RestResponse loginResponse = restBuilder.post("http://localhost:${serverPort}/api/login") {
            json {
                username = TestUtils.admin
                password = TestUtils.admin
            }
        }
        String refreshToken = loginResponse.json.refresh_token

        when:
        RestResponse response = restBuilder.post("http://localhost:${serverPort}/oauth/access_token") {
            header('Content-Type', 'application/x-www-form-urlencoded')
            body "grant_type=refresh_token&refresh_token=${refreshToken}".toString()
        }

        then:
        response.status == 200
        response.json.access_token != null
    }

    void '只有登录用户可以更改密码，更改成功后有短信通知'() {
        setup:
        RestBuilder rest = new RestBuilder()
        User user
        User.withNewTransaction {
            user = TestUtils.createUser('ROLE_YH', '13500000001')
        }

        when: 'not login'
        RestResponse response = rest.put("http://localhost:${serverPort}/api/changePassword") {
            json {
                oldPassword = '13500000001'
                newPassword = 'password2'
            }
        }

        then:
        response.status == 401
        SmsLog.count() == 0

        when: 'login'
        String oldPass = user.password
        String jwt = TestUtils.login(serverPort, '13500000001', '13500000001')
        response = rest.put("http://localhost:${serverPort}/api/changePassword") {
            header('Authorization', "Bearer ${jwt}")
            json {
                oldPassword = '13500000001'
                newPassword = 'password2'
            }
        }

        then:
        response.status == 200
        user.refresh().password != oldPass
        SmsLog.countByUsername('13500000001') == 1
    }

    void '只有登录用户可以看到自己的信息'() {
        setup:
        RestBuilder rest = new RestBuilder()
        User.withNewTransaction {
            TestUtils.createUser('ROLE_YH', '13500000001')
        }

        when: 'not login'
        RestResponse response = rest.get("http://localhost:${serverPort}/api/self")

        then:
        response.status == 401

        when: 'login'
        String jwt = TestUtils.login(serverPort, '13500000001', '13500000001')
        response = rest.get("http://localhost:${serverPort}/api/self") {
            header('Authorization', "Bearer ${jwt}")
        }

        then:
        response.status == 200
        response.json.username == '13500000001'
        response.json.displayName == '13500000001'
    }

    void '只有登录用户可以修改自己的信息'() {
        setup:
        RestBuilder rest = new RestBuilder()
        RestResponse response
        User user
        User.withNewTransaction {
            user = TestUtils.createUser('ROLE_YH', '13500000001')
        }

        when: 'not login'
        response = rest.put("http://localhost:${serverPort}/api/updatePersonalInfo") {
            json {
                displayName = 'newName'
            }
        }

        then:
        response.status == 401

        when: 'login'
        String jwt = TestUtils.login(serverPort, '13500000001', '13500000001')
        response = rest.put("http://localhost:${serverPort}/api/updatePersonalInfo") {
            header('Authorization', "Bearer ${jwt}")
            json {
                displayName = 'newName'
            }
        }
        user.refresh()

        then:
        response.status == 200
        user.displayName == 'newName'
    }

    void "注册页面可发送验证码"() {
        setup:
        RestBuilder rest = new RestBuilder()

        when: 'no username'
        RestResponse response = rest.post("http://localhost:${serverPort}/api/sendSmsCode") {
            json {
                username = ''
            }
        }

        then:
        response.status == 400

        when: 'has username'
        response = rest.post("http://localhost:${serverPort}/api/sendSmsCode") {
            json {
                username = '12345678901'
            }
        }
        then:
        response.status == 200
        SmsLog.countByUsername('12345678901') == 1
    }

}
