package top.dteam.earth.backend.operation

import grails.plugin.springsecurity.SpringSecurityService
import top.dteam.earth.backend.user.User
import top.dteam.earth.backend.util.AliyunOSSUtil

class AliyunOSSController {

    SpringSecurityService springSecurityService

    def getUploadAuthority() {
        User user = springSecurityService.currentUser as User
        respond AliyunOSSUtil.getUploadAuthority(user.username)
    }

}
