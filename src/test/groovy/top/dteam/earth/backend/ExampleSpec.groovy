package top.dteam.earth.backend

import grails.test.hibernate.HibernateSpec

class ExampleSpec extends HibernateSpec {

    List<Class> getDomainClasses() { [Example] }

    void "test something"() {
        setup:
        new Example(kvPair: [key: 'value'], strings: ['1', '2'].toArray()).save(flush: true)

        when:
        Example myDomain = Example.list()[0]

        then:
        myDomain.dateCreated
        myDomain.kvPair.key == 'value'
        myDomain.strings == ['1', '2']
    }
}
