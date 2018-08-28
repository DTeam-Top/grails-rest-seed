package top.dteam.earth.backend.utils

import grails.core.GrailsApplication
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.util.Holders
import top.dteam.earth.backend.user.*

class TestUtils {

    static GrailsApplication grailsApplication = Holders.grailsApplication
    static UserService userService = grailsApplication.mainContext.getBean('userService')

    static void initEnv() {
        initAllRoles()
        createUser('ROLE_ADMIN', 'admin')
    }

    static void clearEnv() {
        LoginHistory.executeUpdate('delete from LoginHistory')
        UserRole.executeUpdate('delete from UserRole')
        Role.executeUpdate('delete from Role')
        User.executeUpdate('delete from User')
    }

    static String login(int serverPort, String user, String pwd) {
        String loginEndpointUrl = grailsApplication.config.grails.plugin.springsecurity.rest.login.endpointUrl
        String loginUrl = "http://localhost:${serverPort}${loginEndpointUrl}"
        RestBuilder restBuilder = new RestBuilder()

        RestResponse response = restBuilder.post(loginUrl) {
            json {
                username = user
                password = pwd
            }
        }

        if (response.status == 200) {
            return response.json.access_token
        } else {
            throw new RuntimeException("Login failed for user ${user} with password ${pwd}.")
        }
    }

    static void initAllRoles() {
        ['ROLE_ADMIN'].each {
            new Role(authority: it).save()
        }
    }

    static User createUser(String role, String name) {
        User user = new User(username: name, password: name, displayName: name)
        userService.createUserWithRole(user, role)
        user
    }

}
