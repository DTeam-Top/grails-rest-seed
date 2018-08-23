package top.dteam.earth.backend.user

import groovy.transform.EqualsAndHashCode

import java.time.LocalDateTime

@EqualsAndHashCode
class LoginHistory implements Serializable {

    private static final long serialVersionUID = 1

    User user
    LocalDateTime dateCreated

    static constraints = {
        user nullable: false
    }

    static mapping = {
        comment '登录历史'
        version false
        user comment: '用户'
        dateCreated comment: '登录时间'
        id composite: ['user', 'dateCreated']
    }
}
