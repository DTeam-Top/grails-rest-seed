package top.dteam.earth.backend.user

import grails.gorm.transactions.Rollback
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import grails.util.Holders
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification
import top.dteam.earth.backend.utils.TestUtils

@Integration
@Rollback
class UserFunctionalSpec extends Specification {

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
        String loginEndpointUrl = Holders.grailsApplication.config.grails.plugin.springsecurity.rest.login.endpointUrl
        String loginUrl = "http://localhost:${serverPort}${loginEndpointUrl}"
        RestBuilder restBuilder = new RestBuilder()
        RestResponse response = restBuilder.post(loginUrl) {
            json {
                username = 'admin'
                password = 'admin'
            }
        }

        then:
        response.status == 200
        response.json.displayName
    }

}
