package top.dteam.earth.backend.user

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification

@Integration
@Rollback
class UserFunctionalSpec extends Specification {

    void "password在持久化后需要加密"() {
        setup:
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()

        when: 'insert'
        User user = new User(username: 13572211111, password: '123456', displayName: 'user')
                .save(flush: true)

        then:
        user.password != '123456'
        encoder.matches('123456', user.password)

        when: 'update'
        user.password = 'abcdef'
        user.save(flush: true)

        then:
        user.password != 'abcdef'
        encoder.matches('abcdef', user.password)
    }

}
