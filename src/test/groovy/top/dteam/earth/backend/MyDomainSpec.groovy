package top.dteam.earth.backend

import grails.test.hibernate.HibernateSpec

class MyDomainSpec extends HibernateSpec {

    List<Class> getDomainClasses() { [MyDomain] }

    void "test something"() {
        setup:
        new MyDomain(kvPair: [key: 'value'], strings: ['1', '2'].toArray()).save(flush: true)

        when:
        MyDomain myDomain = MyDomain.list()[0]

        then:
        myDomain.dateCreated
        myDomain.kvPair.key == 'value'
        myDomain.strings == ['1', '2']
    }
}
