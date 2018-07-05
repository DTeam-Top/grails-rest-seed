package top.dteam.earth.backend.user

import grails.plugin.springsecurity.rest.RestTokenCreationEvent
import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener

@Slf4j
@CompileStatic
class LoginEventListener implements ApplicationListener<RestTokenCreationEvent> {
    @Autowired
    LoginHistoryService loginHistoryService

    @Autowired
    UserService userService

    @Override
    void onApplicationEvent(RestTokenCreationEvent event) {
        GrailsUser userDetails = event.principal as GrailsUser
        log.debug("User {} login successfully.", userDetails.username)

        int userId = userDetails.id as int
        loginHistoryService.save(new LoginHistory(user: userService.get(userId)))
    }
}
