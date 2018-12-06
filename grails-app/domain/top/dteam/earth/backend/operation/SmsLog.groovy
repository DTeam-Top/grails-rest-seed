package top.dteam.earth.backend.operation

import grails.rest.Resource
import net.kaleidos.hibernate.usertype.JsonbMapType

import java.time.LocalDateTime

@Resource(uri = '/api/smslogs', readOnly = true)
class SmsLog {

    String username
    String notificationType
    String message
    Map params
    boolean treated = false
    LocalDateTime dateCreated
    LocalDateTime lastUpdated

    static constraints = {
        username nullable: false
        notificationType nullable: false, maxSize: 20, inList: availableTypes()
        message nullable: false, blank: false, maxSize: 500
        params nullable: false
    }

    static mapping = {
        comment '短消息历史'
        version false
        username comment: '通知用户名称', index: 'idx_smslog_username'
        notificationType comment: '通知类型'
        message comment: '消息内容'
        params comment: '短信模板参数', type: JsonbMapType
        // 创建createIndex DSL，通过liquibase的modify sql创建为partial index
        // 参见: http://forum.liquibase.org/topic/how-to-create-conditional-indexes-using-createindex
        treated comment: '是否发送', index: 'idx_smslog_treated'
        dateCreated comment: '创建时间', index: 'idx_smslog_date_created'
        lastUpdated comment: '发送时间'
    }

    static List availableTypes() {
        ['验证码', '新密码', '重置密码']
    }

    static String genMessage(String type, Map params = [:]) {
        if (type == '验证码') {
            return "您正在申请手机注册，验证码为：******，5分钟内有效！"
        } else if (type == '重置密码') {
            return "您好，您的密码已经重置为******，请及时登录并修改密码。"
        } else if (type == '新密码') {
            return "您好，您的密码已经成功修改，请牢记新密码。"
        } else {
            return null
        }
    }

}

