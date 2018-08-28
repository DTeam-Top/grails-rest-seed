import grails.plugin.springsecurity.SpringSecurityService
import top.dteam.earth.backend.user.LoginEventListener
import top.dteam.earth.backend.user.LoginResponseJsonRender
import top.dteam.earth.backend.user.MyUserDetailsService
import top.dteam.earth.backend.user.UserPasswordEncoderListener

beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener)
    userDetailsService(MyUserDetailsService)
    accessTokenJsonRenderer(LoginResponseJsonRender) {
        usernamePropertyName = 'username'
        tokenPropertyName = 'access_token'
        authoritiesPropertyName = 'roles'
        useBearerToken = true
    }
    springSecurityService(SpringSecurityService) {
        authenticationTrustResolver = ref('authenticationTrustResolver')
        grailsApplication = application
        objectDefinitionSource = ref('objectDefinitionSource')
        passwordEncoder = ref('passwordEncoder')
    }
    loginEventListener(LoginEventListener)
}
