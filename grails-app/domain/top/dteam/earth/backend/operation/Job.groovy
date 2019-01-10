package top.dteam.earth.backend.operation

import grails.rest.Resource
import net.kaleidos.hibernate.usertype.JsonbMapType

import java.time.LocalDateTime

@Resource(uri = '/api/jobs', readOnly = true)
class Job {

    String topic
    int priority = 5
    Map body
    String status = 'CREATED'
    Map result
    int retry = 0
    LocalDateTime dateCreated
    LocalDateTime lastUpdated

    static constraints = {
        topic nullable: false, blank: false, inList: availableTopics()
        priority min: 1, max: 10
        body nullable: false, validator: { val, obj -> validateBodyWithTopic(val, obj.topic) }
        status nullable: false, blank: false, inList: availableStatus()
        result nullable: true
    }

    // TODO：创建联合索引，(priority desc, date_created, status = 'CREATED')
    static mapping = {
        comment '任务队列'
        table 'myjob'
        version false
        topic comment: '任务主题'
        priority comment: '优先级'
        body comment: '任务主体', type: JsonbMapType
        status comment: '任务状态'
        result comment: '任务结果'
        retry comment: '重试次数'
        dateCreated comment: '创建时间'
        lastUpdated comment: '最近更新'
    }

    static List availableTopics() {
        ['SMS', 'CALLBACK']
    }

    static List availableStatus() {
        ['CREATED', 'PROCESSING', 'SUCCESS', 'FAILURE']
    }

    // 如果job需要有回调，则body中需包含callback
    static boolean validateBodyWithTopic(Map body, String topic) {
        if (topic == 'SMS') {
            return body.containsKey('message') &&
                    body.containsKey('notificationType') &&
                    body.containsKey('username') &&
                    body.containsKey('params') &&
                    body.params instanceof Map
        } else if (topic == 'CALLBACK') {
            return body.containsKey('source') &&    // 源job的id
                    body.containsKey('result') &&   // 源job的结果
                    body.result instanceof Map
        }

        true
    }

}
