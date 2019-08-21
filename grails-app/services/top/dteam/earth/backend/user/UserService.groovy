package top.dteam.earth.backend.user

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import top.dteam.earth.backend.converter.DateTimeValueConverter
import top.dteam.earth.backend.operation.JobService

@Service(User)
abstract class UserService {

    JobService jobService

    @Autowired
    DateTimeValueConverter dateTimeValueConverter

    abstract User get(Serializable id)

    abstract Long count()

    abstract User save(User user)

    abstract User findByUsername(String username)

    @Transactional(readOnly = true)
    List<User> list(Map args = [:]) {
        User.createCriteria().list(args) {
            if (args.containsKey('enabled')) {
                eq('enabled', Boolean.valueOf(args.enabled))
            }

            if (args.username) {
                // 手机号通常只有前缀匹配的需求
                like('username', "${args.username}%")
            }

            if (args.dateCreatedStart) {
                gte('dateCreated', dateTimeValueConverter.convert(args.dateCreatedStart))
            }

            if (args.dateCreatedEnd) {
                lte('dateCreated', dateTimeValueConverter.convert(args.dateCreatedEnd))
            }

            if (args.role) {
                sqlRestriction '''
                    EXISTS ( SELECT 1 FROM user_role
                        JOIN role ON user_role.role_id = role.id
                        WHERE user_role.user_id = {alias}.id AND
                            role.authority = ?
                    )
                ''', [args.role]
            }
        }
    }

    @Transactional
    void register(User user) {
        save(user)
        new UserRole(user: user, role: Role.findByAuthority('ROLE_YH')).save()
    }

    @Transactional
    User createUserWithRole(User user, String role) {
        save(user)
        new UserRole(user: user, role: Role.findByAuthority(role)).save()
        user
    }

    @Transactional
    User createUserWithRoles(User user, List<String> roles) {
        save(user)
        roles.each { role ->
            new UserRole(user: user, role: Role.findByAuthority(role)).save()
        }
        user
    }

    @Transactional
    void freeze(User user) {
        if (user.enabled) {
            user.enabled = false
            save(user)
        }
    }

    @Transactional
    void unfreeze(User user) {
        if (!user.enabled) {
            user.enabled = true
            save(user)
        }
    }

    @Transactional
    void resetPassword(User user) {
        user.password = RandomStringUtils.randomNumeric(6)
        save(user)
        jobService.saveSmsJob(user.username, JobService.SMS_PASSWORD_REST, [password: user.password])
    }

}
