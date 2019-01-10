package top.dteam.earth.backend.operation

import grails.testing.gorm.DataTest
import spock.lang.Specification

class JobSpec extends Specification implements DataTest {

    Class[] getDomainClassesToMock() {
        [Job].toArray()
    }

    void 'Job Topic为SMS时，Body的键值必须同时包含message、username、notificationType、params，且params的值必须是Map类型'() {
        expect:
        !new Job(topic: 'SMS', body: []).validate(['body'])
        !new Job(topic: 'SMS', body: [message: '', username: '', notificationType: '']).validate(['body'])
        !new Job(topic: 'SMS', body: [message: '', params: '']).validate(['body'])
        !new Job(topic: 'SMS', body: [username: '', notificationType: '', params: '']).validate(['body'])
        !new Job(topic: 'SMS', body: [message: '', username: '', notificationType: '', params: '']).validate(['body'])
        new Job(topic: 'SMS', body: [message: '', username: '', notificationType: '', params: [:]]).validate(['body'])
    }

    void 'Job Topic为CALLBACK时，Body的键值必须同时包含source、result、callback，且result的值必须是Map类型'() {
        expect:
        !new Job(topic: 'CALLBACK', body: []).validate(['body'])
        !new Job(topic: 'CALLBACK', body: [source: 1]).validate(['body'])
        !new Job(topic: 'CALLBACK', body: [result: [:]]).validate(['body'])
        !new Job(topic: 'CALLBACK', body: [callback: '']).validate(['body'])
        !new Job(topic: 'CALLBACK', body: [source: 1, callback: '', result: '']).validate(['body'])
        new Job(topic: 'CALLBACK', body: [source: 1, callback: '', result: [:]]).validate(['body'])
    }

}
