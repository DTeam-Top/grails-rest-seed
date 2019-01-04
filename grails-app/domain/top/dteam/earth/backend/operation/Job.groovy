package top.dteam.earth.backend.operation

import grails.rest.Resource
import net.kaleidos.hibernate.usertype.JsonbMapType

import java.time.LocalDateTime

@Resource(uri = '/api/jobs', readOnly = true)
class Job {

    String topic
    Map body
    boolean processed = false
    Map result
    String callback
    boolean invoked = false
    LocalDateTime dateCreated

    static constraints = {
        topic nullable: false, blank: false, inList: availableTopics()
        body nullable: false, validator: { val, obj -> validateBodyWithTopic(val, obj.topic) }
        result nullable: true
        callback nullable: true, maxSize: 500
    }

    static mapping = {
        comment '任务队列'
        table 'myjob'
        version false
        topic comment: '任务主题', index: 'idx_myjob_topic'
        body comment: '任务主体', type: JsonbMapType
        // TODO: 创建partial index, processed = false
        processed comment: '是否处理', index: 'idx_myjob_processed'
        result comment: '任务结果'
        // TODO: 创建partial index, callback is not null
        callback comment: '任务结果回调地址'
        // TODO: 创建partial index, invoked = false
        invoked comment: '是否处理回调', index: 'idx_myjob_invoked'
        dateCreated comment: '创建时间', index: 'idx_myjob_date_created'
    }

    static List availableTopics() {
        ['SMS']
    }

    static boolean validateBodyWithTopic(Map body, String topic) {
        if (topic == 'SMS') {
            return body.containsKey('message') &&
                    body.containsKey('notificationType') &&
                    body.containsKey('username') &&
                    body.containsKey('params') &&
                    body.params instanceof Map
        }
        true
    }

}
