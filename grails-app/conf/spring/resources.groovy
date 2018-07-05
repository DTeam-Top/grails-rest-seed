import top.dteam.earth.backend.user.LoginEventListener
import top.dteam.earth.backend.user.UserPasswordEncoderListener
import grails.plugin.springsecurity.SpringSecurityService

// Place your Spring DSL code here
beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener)
    springSecurityService(SpringSecurityService) {
        authenticationTrustResolver = ref('authenticationTrustResolver')
        grailsApplication = application
        objectDefinitionSource = ref('objectDefinitionSource')
        passwordEncoder = ref('passwordEncoder')
    }
    loginEventListener(LoginEventListener)
}
