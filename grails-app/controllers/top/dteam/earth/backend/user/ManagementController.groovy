package top.dteam.earth.backend.user

import grails.plugin.springsecurity.SpringSecurityService
import grails.validation.ValidationException
import groovy.util.logging.Slf4j

import static org.springframework.http.HttpStatus.*

// Admin用来管理用户
@Slf4j
class ManagementController {

    UserService userService
    SpringSecurityService springSecurityService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", resetPassword: "PUT"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond userService.list(params), view: '/user/index'
    }

    def show(Long id) {
        User user = userService.get(id)

        if (!user) {
            render(status: NOT_FOUND)
            return
        }

        respond user
    }

    def save() {
        String role = request.JSON.role
        User currentLoginUser = springSecurityService.currentUser as User

        if (currentLoginUser.hasRole('ROLE_KF') && role == 'ROLE_ADMIN') {
            def message = [message: '客服不能创建管理员']
            respond message, status: FORBIDDEN
            return
        }

        User user = new User()
        user.properties = request.JSON

        try {
            userService.createUserWithRole(user, role)
        } catch (ValidationException e) {
            respond user.errors
            return
        }

        render status: CREATED
    }

    def update(Long id) {
        User user = userService.get(id)
        if (!user) {
            render status: NOT_FOUND
            return
        }

        User currentLoginUser = springSecurityService.currentUser as User
        if (currentLoginUser.hasRole('ROLE_KF') && user.hasRole('ROLE_ADMIN')) {
            def message = [message: '客服修改创建管理员']
            respond message, status: FORBIDDEN
            return
        }

        try {
            updateUserByAction(params.operation, user)
        } catch (ValidationException e) {
            respond user.errors, status: BAD_REQUEST
            return
        }

        render status: OK
    }

    //忘记密码不需要验证
    def resetPassword() {
        User user = userService.findByUsername(request.JSON.username)

        if (!user) {
            render status: NOT_FOUND
            return
        }

        try {
            updateUserByAction('resetPassword', user)
        } catch (ValidationException e) {
            respond user.errors
            return
        }

        render status: OK
    }

    private void updateUserByAction(String operation, User user) {
        if (operation == 'frozen') {
            userService.freeze(user)
        } else if (operation == 'unfrozen') {
            userService.unfreeze(user)
        } else if (operation == 'resetPassword') {
            userService.resetPassword(user)
        } else {
            log.error('不明操作类型: {}', operation)
            render status: FORBIDDEN
        }
    }

}
