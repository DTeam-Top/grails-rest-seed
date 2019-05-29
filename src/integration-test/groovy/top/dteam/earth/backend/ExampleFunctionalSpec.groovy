package top.dteam.earth.backend

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification
import spock.lang.Unroll

import java.time.OffsetDateTime

@Integration
@Rollback
class ExampleFunctionalSpec extends Specification {

    @Unroll
    void 'datetime字段应该同时兼容带时区格式和不带时区格式'() {
        setup:
        RestBuilder rest = new RestBuilder()
        TimeZone defaultZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone('UTC'))
        Example example
        Example.withNewTransaction {
            example = new Example(kvPair: [k: 'v'], strings: ['a'], dateTimeField: OffsetDateTime.now()).save()
        }

        when:
        RestResponse response = rest.put("http://localhost:${serverPort}/example/${example.id}") {
            json {
                dateTimeField = dateTimeString
            }
        }
        example.refresh()

        then:
        response.status == 200
        example.dateTimeField.isEqual(OffsetDateTime.parse(expectDateTime))

        cleanup:
        TimeZone.setDefault(defaultZone)

        where:
        dateTimeString               | expectDateTime
        '2018-01-01 01:01:01'        | '2018-01-01T01:01:01Z'
        '2018-01-01 01:01:01.001'    | '2018-01-01T01:01:01.001Z'
        '2018-01-01 01:01:01.001+08' | '2018-01-01T01:01:01.001+08:00'
        '2018-01-01T01:01:01.001+08' | '2018-01-01T01:01:01.001+08:00'
    }

}
