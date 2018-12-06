package top.dteam.earth.backend.operation


import grails.gorm.services.Service
import grails.gorm.transactions.Transactional

@Service(SmsLog)
abstract class SmsLogService {

    abstract SmsLog save(SmsLog smsLog)

    private SmsLog genSmsLog(String username, String notificationType, Map params = [:]) {
        SmsLog smsLog = new SmsLog(username: username, notificationType: notificationType
                , message: SmsLog.genMessage(notificationType, params), params: params)

        save(smsLog)
    }

    // 发送验证码短信
    @Transactional
    SmsLog saveSmsCode(String username, String code) {
        genSmsLog(username, '验证码', [code: code])
    }

    // 发送重置密码短信
    @Transactional
    SmsLog saveResetPassword(String username, String password) {
        genSmsLog(username, '重置密码', [password: password])
    }

    // 发送新密码短信
    @Transactional
    SmsLog saveNewPassword(String username) {
        genSmsLog(username, '新密码')
    }

}