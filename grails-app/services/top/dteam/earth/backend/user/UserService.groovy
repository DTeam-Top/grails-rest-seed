package top.dteam.earth.backend.user

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional

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
    void createUserWithRole(User user, String role) {
        user.save()
        new UserRole(user: user, role: Role.findByAuthority(role)).save()
    }

}