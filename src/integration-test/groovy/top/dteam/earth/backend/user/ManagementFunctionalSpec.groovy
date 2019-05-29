package top.dteam.earth.backend.user

import grails.gorm.transactions.Rollback
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import spock.lang.Specification
import spock.lang.Unroll
import top.dteam.earth.backend.operation.Job
import top.dteam.earth.backend.utils.TestUtils

import java.time.OffsetDateTime

@Integration
@Rollback
class ManagementFunctionalSpec extends Specification {

    void setup() {
        TestUtils.initEnv()
    }

    void cleanup() {
        TestUtils.clearEnv()
    }

    @Unroll
    void '只有管理员才能看到所有用户列表'() {
        setup:
        RestBuilder rest = new RestBuilder()
        RestResponse response
        User.withNewTransaction {
            TestUtils.createUser(role, '13500000001')
        }

        when:
        String userJwt = TestUtils.login(serverPort, '13500000001', '13500000001')
        response = rest.get("http://localhost:${serverPort}/api/users") {
            header('Authorization', "Bearer ${userJwt}")
        }

        then:
        response.status == statusCode
        response.json?.userCount == userCount

        where:
        role         | statusCode | userCount
        'ROLE_ADMIN' | 200        | 2
        'ROLE_YH'    | 403        | null
    }

    @Unroll
    void '只有管理员才能看到其他人信息'() {
        setup:
        RestBuilder rest = new RestBuilder()
        RestResponse response
        User user
        User.withNewTransaction {
            TestUtils.createUser(role, '13500000001')
            user = TestUtils.createUser('ROLE_YH', '13500000002')
        }

        when:
        String userJwt = TestUtils.login(serverPort, '13500000001', '13500000001')
        response = rest.get("http://localhost:${serverPort}/api/users/${user.id}") {
            header('Authorization', "Bearer ${userJwt}")
        }

        then:
        response.status == statusCode
        response.json?.username == result

        where:
        role         | statusCode | result
        'ROLE_ADMIN' | 200        | '13500000002'
        'ROLE_YH'    | 403        | null
    }

    @Unroll
    void '管理员可以添加任意角色用户'() {
        setup:
        RestBuilder rest = new RestBuilder()
        String jwt = TestUtils.login(serverPort, TestUtils.admin, TestUtils.admin)

        when:
        RestResponse response = rest.post("http://localhost:${serverPort}/api/users/") {
            header('Authorization', "Bearer ${jwt}")
            json {
                username = '12345678901'
                password = 'test'
                displayName = '12345678901'
                role = roleAdded
            }
        }

        then:
        response.status == 201
        User.findByUsername('12345678901').hasRole(roleAdded)

        where:
        roleAdded << Role.validRoles()
    }

    @Unroll
    void '管理员可以对任意角色用户冻结、解冻和重置密码'() {
        setup:
        RestBuilder rest = new RestBuilder()
        String jwt = TestUtils.login(serverPort, TestUtils.admin, TestUtils.admin)
        User user
        RestResponse response
        User.withNewTransaction {
            user = TestUtils.createUser(role, '13500000001')
        }

        when: '冻结'
        response = rest.put("http://localhost:${serverPort}/api/users/${user.id}?operation=frozen") {
            header('Authorization', "Bearer ${jwt}")
        }
        user.refresh()

        then:
        response.status == 200
        !user.enabled

        when: '解冻'
        response = rest.put("http://localhost:${serverPort}/api/users/${user.id}?operation=unfrozen") {
            header('Authorization', "Bearer ${jwt}")
        }

        then:
        response.status == 200
        user.refresh().enabled

        when: '重置密码'
        String oldPassword = user.password
        response = rest.put("http://localhost:${serverPort}/api/users/${user.id}?operation=resetPassword") {
            header('Authorization', "Bearer ${jwt}")
        }
        user.refresh()

        then:
        response.status == 200
        user.password != oldPassword
        Job.findByTopic('SMS').body.username == user.username

        where:
        role << ['ROLE_YH']
    }

    void 'resetPassword：匿名用户可以根据手机号重置密码'() {
        setup:
        RestBuilder rest = new RestBuilder()
        RestResponse response
        User user
        User.withNewTransaction {
            user = TestUtils.createUser('ROLE_YH', '13500000002')
        }

        when: '重置密码'
        String oldPassword = user.password
        response = rest.put("http://localhost:${serverPort}/api/users/resetPassword") {
            json {
                username = user.username
            }
        }
        user.refresh()

        then:
        response.status == 200
        user.password != oldPassword
        Job.findByTopic('SMS').body.username == user.username
    }

    void '管理员应该可以按角色筛选用户'() {
        setup:
        RestBuilder rest = new RestBuilder()
        RestResponse responseAdmin
        RestResponse responseKf
        User.withNewTransaction {
            TestUtils.createUser('ROLE_YH', '13500000002')
        }
        String jwtAdmin = TestUtils.login(serverPort, '13500000000', '13500000000')

        when:
        responseAdmin = rest.get("http://localhost:${serverPort}/api/users?sort=id&order=desc&role=${role}") {
            header('Authorization', "Bearer ${jwtAdmin}")
        }

        then:
        responseAdmin.status == 200
        responseAdmin.json.userCount == userCount
        responseAdmin.json.userList.collect { it.username } == userList

        where:
        role         | userCount | userList
        'ROLE_ADMIN' | 1         | ['13500000000']
        'ROLE_YH'    | 1         | ['13500000002']
    }

    @Unroll
    void '管理员应该可以按enabled,username,dateCreated筛选用户'() {
        setup:
        TimeZone defaultZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone('UTC'))
        RestBuilder rest = new RestBuilder()
        RestResponse responseAdmin
        User.withNewTransaction {
            User admin = User.findByUsername('13500000000')
            admin.dateCreated = OffsetDateTime.parse('1970-01-01T00:00:00Z')
            admin.save()
            User u1 = TestUtils.createUser('ROLE_YH', '13500000002')
            u1.dateCreated = OffsetDateTime.parse('2018-10-10T01:00:00Z')
            u1.enabled = false
            u1.save()
            User u2 = TestUtils.createUser('ROLE_YH', '13500000003')
            u2.dateCreated = OffsetDateTime.parse('2018-10-11T01:00:00Z')
            u2.save()
            User u3 = TestUtils.createUser('ROLE_YH', '13500000004')
            u3.dateCreated = OffsetDateTime.parse('2018-10-12T01:00:00Z')
            u3.save()
        }
        String jwtAdmin = TestUtils.login(serverPort, '13500000000', '13500000000')

        when:
        responseAdmin = rest.get("http://localhost:${serverPort}/api/users?sort=id&order=desc&${query}") {
            header('Authorization', "Bearer ${jwtAdmin}")
        }

        then:
        responseAdmin.status == 200
        responseAdmin.json.userCount == userCount
        responseAdmin.json.userList.collect { it.username } == userList

        cleanup:
        TimeZone.setDefault(defaultZone)

        where:
        query                                                                      | userCount | userList
        'enabled=true'                                                             | 3         | ['13500000004', '13500000003', '13500000000']
        'enabled=false'                                                            | 1         | ['13500000002']
        'username=13500000002'                                                     | 1         | ['13500000002']
        'username=1360000'                                                         | 0         | []
        'dateCreatedStart=2018-10-11T00:00:00Z'                                    | 2         | ['13500000004', '13500000003']
        'dateCreatedEnd=2018-10-11 02:00:00'                                       | 3         | ['13500000003', '13500000002', '13500000000']
        'dateCreatedStart=2018-10-10T00:00:00&dateCreatedEnd=2018-10-11T23:59:59Z' | 2         | ['13500000003', '13500000002']
        'dateCreatedStart=2018-10-20T00:00:00Z&dateCreatedEnd=2018-10-21T23:59:59' | 0         | []
    }

}
