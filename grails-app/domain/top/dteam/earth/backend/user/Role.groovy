package top.dteam.earth.backend.user

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@GrailsCompileStatic
@EqualsAndHashCode(includes = 'authority')
@ToString(includes = 'authority', includeNames = true, includePackage = false)
class Role implements Serializable {

    private static final long serialVersionUID = 1

    String authority

    static constraints = {
        authority nullable: false, blank: false, unique: true, inList: validRoles()
    }

    static mapping = {
        cache true
        comment '角色'
        authority comment: '角色名'
    }

    static List validRoles() {
        [
                'ROLE_ADMIN', // 管理员
                'ROLE_YY', // 运营
                'ROLE_KF', // 客服
                'ROLE_CW', // 财务
                'ROLE_GYS', // 供应商
                'ROLE_YJDL', // 一级代理
                'ROLE_EJDL', // 二级代理
                'ROLE_EJDLYG', // 二级代理员工
                'ROLE_YH' // 用户
        ]
    }
}
