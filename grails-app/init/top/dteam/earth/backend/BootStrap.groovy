package top.dteam.earth.backend

import groovy.util.logging.Slf4j
import org.springframework.boot.info.GitProperties
import top.dteam.earth.backend.user.Role
import top.dteam.earth.backend.user.User
import top.dteam.earth.backend.user.UserService

@Slf4j
class BootStrap {

    GitProperties gitProperties
    UserService userService

    def init = { servletContext ->
        log.info('Application running at commit: {}, branch: {}, commit time: {}, build time: {}'
                , gitProperties.shortCommitId
                , gitProperties.branch
                , gitProperties.commitTime
                , gitProperties.getDate('build.time'))

        environments {
            development {
                Role.validRoles().each {
                    new Role(authority: it).save()
                }
                User user = new User(username: '11111111111', password: 'admin', displayName: 'admin')
                userService.createUserWithRole(user, 'ROLE_ADMIN')
            }
        }
    }

    def destroy = {
    }

}
