package top.dteam.earth.backend.user

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional
import org.apache.commons.lang.RandomStringUtils

@Service(User)
abstract class UserService {

    abstract User get(Serializable id)

    abstract List<User> list(Map args)

    abstract Long count()

    abstract User save(User user)

    abstract User findByUsername(String username)

    @Transactional
    void register(User user) {
        user.save()
        new UserRole(user: user, role: Role.findByAuthority('ROLE_YH')).save()
    }

    @Transactional
    User createUserWithRole(User user, String role) {
        user.save()
        new UserRole(user: user, role: Role.findByAuthority(role)).save()
        user
    }

    @Transactional
    User createUserWithRoles(User user, List<String> roles) {
        user.save()
        roles.each { role ->
            new UserRole(user: user, role: Role.findByAuthority(role)).save()
        }
        user
    }

    @Transactional
    void freeze(User user) {
        if (user.enabled) {
            user.enabled = false
            user.save()
        }
    }

    @Transactional
    void unfreeze(User user) {
        if (!user.enabled) {
            user.enabled = true
            user.save()
        }
    }

    @Transactional
    void resetPassword(User user) {
        user.password = RandomStringUtils.randomNumeric(6)
        user.save()
    }

}