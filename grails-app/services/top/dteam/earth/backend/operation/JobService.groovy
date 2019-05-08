package top.dteam.earth.backend.operation

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional

@Service(Job)
abstract class JobService {

    final static String SMS_NEWPASSWORD = '验证码'
    final static String SMS_PASSWORD_REST = '重置密码'
    final static String SMS_CODE = '新密码'

    abstract List<Job> findAllByTopic(String topic, Map params)

    abstract Job save(Job job)

    @Transactional
    Job saveSmsJob(String username, String notificationType, Map params = [:]) {
        new Job(topic: 'SMS', body: [username          : username
                                     , notificationType: notificationType
                                     , message         : smsMessage(notificationType)
                                     , params          : params]).save()
    }

    private String smsMessage(String type) {
        if (type == SMS_CODE) {
            return '您正在申请手机注册，验证码为：******，5分钟内有效！'
        } else if (type == SMS_PASSWORD_REST) {
            return '您好，您的密码已经重置为******，请及时登录并修改密码。'
        } else if (type == SMS_NEWPASSWORD) {
            return '您好，您的密码已经成功修改，请牢记新密码。'
        }

        null
    }

}
