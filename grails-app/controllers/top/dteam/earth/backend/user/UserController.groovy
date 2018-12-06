package top.dteam.earth.backend.user

import grails.plugin.springsecurity.SpringSecurityService
import groovy.util.logging.Slf4j
import org.apache.commons.lang.RandomStringUtils
import org.grails.datastore.mapping.validation.ValidationException
import org.grails.web.json.JSONObject
import org.springframework.security.authentication.encoding.PasswordEncoder
import top.dteam.earth.backend.operation.SmsLogService

import static org.springframework.http.HttpStatus.*

@Slf4j
class UserController {

    SpringSecurityService springSecurityService
    SmsLogService smsLogService
    UserService userService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [register                 : 'POST'
                             , sendSmsCode            : 'POST'
                             , updatePersonalInfo     : 'PUT'
                             , changePassword         : 'PUT'
                             , submitSellerApplication: 'POST'
    ]

    def register() {
        User user = new User()
        user.properties = request.JSON
        try {
            userService.register(user)
        } catch (ValidationException e) {
            log.warn("用户 {} 注册失败: {}", user.username, e.message)
            respond user.errors
            return
        }

        render status: CREATED
    }

    def self() {
        User user = springSecurityService.currentUser
        respond user
    }

    def updatePersonalInfo() {
        User user = springSecurityService.currentUser
        JSONObject json = request.JSON
        bindData(user, json, [include: ['displayName']])

        try {
            userService.save(user)
        } catch (ValidationException e) {
            respond user.errors
            return
        }

        render status: OK
    }

    def changePassword() {
        User user = springSecurityService.currentUser

        try {
            if (request.JSON.oldPassword &&
                    request.JSON.newPassword &&
                    (springSecurityService.passwordEncoder as PasswordEncoder).isPasswordValid(user.password, request.JSON.oldPassword, null)) {
                user.password = request.JSON.newPassword
                user.passwordExpired = false
                userService.save(user)
                smsLogService.saveNewPassword(user.username)
            } else {
                def message = [message: '旧密码不正确']
                respond message, status: BAD_REQUEST
                return
            }
        } catch (ValidationException e) {
            respond user.errors
            return
        }

        render status: OK
    }

    def sendSmsCode() {
        if (!request.JSON.username) {
            render status: BAD_REQUEST
            return
        }

        String smsCode = RandomStringUtils.randomNumeric(6)
        smsLogService.saveSmsCode(request.JSON.username, smsCode)

        Map map = [smsCode: smsCode]
        respond map
    }

}

