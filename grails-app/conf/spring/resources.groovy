import org.grails.databinding.converters.Jsr310ConvertersConfiguration
import top.dteam.earth.backend.converter.DateTimeValueConverter
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
    loginEventListener(LoginEventListener)
    // TODO: 等待官方修复ValueConverter缺失的issue: https://github.com/grails/grails-core/issues/11387
    jsr310DataBinding(Jsr310ConvertersConfiguration) {
        formatStrings = application.config.getProperty('grails.databinding.dateFormats', Set)
    }
    dateTimeValueConverter(DateTimeValueConverter)
}
