package top.dteam.earth.backend.user

import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService
import grails.plugin.springsecurity.userdetails.NoStackUsernameNotFoundException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Transactional
@GrailsCompileStatic
class MyUserDetailsService implements GrailsUserDetailsService {

    static final List NO_ROLES = [new SimpleGrantedAuthority(SpringSecurityUtils.NO_ROLE)]

    @Override
    UserDetails loadUserByUsername(String username, boolean loadRoles)
            throws UsernameNotFoundException {
        loadUserByUsername(username)
    }

    @Transactional(readOnly = true, noRollbackFor = [IllegalArgumentException, UsernameNotFoundException])
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = User.findByUsername(username)
        if (!user) {
            throw new NoStackUsernameNotFoundException()
        }

        Set<Role> roles = user.authorities
        List authorities = roles?.collect {
            new SimpleGrantedAuthority(it.authority)
        } as List

        new MyUserDetails(user.username
                , user.password
                , user.enabled
                , !user.accountExpired
                , !user.passwordExpired
                , !user.accountLocked
                , authorities ?: NO_ROLES
                , user.id
                , user.displayName)
    }

}
