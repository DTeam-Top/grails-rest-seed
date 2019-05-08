package top.dteam.earth.backend.user

import grails.databinding.converters.ValueConverter
import grails.gorm.services.Service
import grails.gorm.transactions.Transactional
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import top.dteam.earth.backend.operation.JobService

@Service(User)
abstract class UserService {

    JobService jobService

    @Qualifier('localDateTimeValueConverter')
    @Autowired
    ValueConverter localDateTimeValueConverter

    abstract User get(Serializable id)

    abstract Long count()

    abstract User save(User user)

    abstract User findByUsername(String username)

    @Transactional(readOnly = true)
    List<User> list(Map args = [:]) {
        User.createCriteria().list(args) {
            if (args.enabled) {
                eq('enabled', Boolean.valueOf(args.enabled))
            }

            if (args.username) {
                // 手机号通常只有前缀匹配的需求
                like('username', "${args.username}%")
            }

            if (args.dateCreatedStart) {
                gte('dateCreated', localDateTimeValueConverter.convert(args.dateCreatedStart.toString()))
            }

            if (args.dateCreatedEnd) {
                lte('dateCreated', localDateTimeValueConverter.convert(args.dateCreatedEnd.toString()))
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
        jobService.saveSmsJob(user.username, JobService.SMS_PASSWORD_REST, [password: user.password])
    }

}
