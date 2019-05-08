package top.dteam.earth.backend.user

import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileStatic
import groovy.transform.ToString
import org.springframework.security.core.GrantedAuthority

@ToString(includeNames = true, includeSuperProperties = true)
@CompileStatic
class MyUserDetails extends GrailsUser {

    final String displayName

    MyUserDetails(String username
                  , String password
                  , boolean enabled
                  , boolean accountNonExpired
                  , boolean credentialsNonExpired
                  , boolean accountNonLocked
                  , Collection<GrantedAuthority> authorities
                  , Object id
                  , String displayName) {
        super(username, password, enabled
                , accountNonExpired, credentialsNonExpired
                , accountNonLocked, authorities, id)
        this.displayName = displayName
    }

}
