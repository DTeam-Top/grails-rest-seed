package top.dteam.earth.backend.user

import grails.converters.JSON
import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.rendering.AccessTokenJsonRenderer
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.util.Assert

@Slf4j
@CompileStatic
class LoginResponseJsonRender implements AccessTokenJsonRenderer {

    String usernamePropertyName
    String tokenPropertyName
    String authoritiesPropertyName

    Boolean useBearerToken

    @Override
    String generateJson(AccessToken accessToken) {
        Assert.isInstanceOf(MyUserDetails, accessToken.principal, 'A MyUserDetails implementation is required')
        MyUserDetails userDetails = accessToken.principal as MyUserDetails

        Map<String, Object> result = [
                (usernamePropertyName)   : userDetails.username,
                (authoritiesPropertyName): accessToken.authorities*.authority,
                userId                   : userDetails.id,
                displayName              : userDetails.displayName
        ]

        if (useBearerToken) {
            result.token_type = 'Bearer'
            result.access_token = accessToken.accessToken

            if (accessToken.expiration) {
                result.expires_in = accessToken.expiration
            }

            if (accessToken.refreshToken) {
                result.refresh_token = accessToken.refreshToken
            }
        } else {
            result[tokenPropertyName] = accessToken.accessToken
        }

        JSON jsonResult = result as JSON

        log.debug "Generated JSON: ${jsonResult.toString()}"

        jsonResult.toString()
    }

}
