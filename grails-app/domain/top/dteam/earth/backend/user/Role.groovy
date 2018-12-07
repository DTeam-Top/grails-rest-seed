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

    static List<String> validRoles() {
        [
                'ROLE_ADMIN'  // 管理员
                , 'ROLE_YH'   // 用户
        ]
    }

}
