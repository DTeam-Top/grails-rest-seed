package top.dteam.earth.backend.operation

import grails.gorm.transactions.Rollback
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import spock.lang.Specification
import spock.lang.Unroll
import top.dteam.earth.backend.user.Role
import top.dteam.earth.backend.user.User
import top.dteam.earth.backend.utils.TestUtils

@Integration
@Rollback
class AliyunOSSFunctionalSpec extends Specification {

    void setup() {
        TestUtils.initEnv()
    }

    void cleanup() {
        TestUtils.clearEnv()
    }

    void '匿名用户无权获取上传权限'() {
        setup:
        RestBuilder rest = new RestBuilder()
        RestResponse response

        when:
        response = rest.get("http://localhost:${serverPort}/api/getUploadAuthority")

        then:
        response.status == 401
    }

    @Unroll
    void "所有登录用户均有权限获取上传权限: #role"() {
        setup:
        RestBuilder rest = new RestBuilder()
        RestResponse response
        User.withNewTransaction {
            TestUtils.createUser(role, '13500000001')
        }
        String jwt = TestUtils.login(serverPort, '13500000001', '13500000001')

        when:
        response = rest.get("http://localhost:${serverPort}/api/getUploadAuthority") {
            header('Authorization', "Bearer ${jwt}")
        }

        then:
        response.status == 200
        response.json.containsKey('accessKeyId')
        response.json.containsKey('policy')
        response.json.containsKey('signature')
        response.json.containsKey('dir')
        response.json.containsKey('host')
        response.json.containsKey('expire')
        response.json.containsKey('cdnUrl')

        where:
        role << Role.validRoles()
    }

}
