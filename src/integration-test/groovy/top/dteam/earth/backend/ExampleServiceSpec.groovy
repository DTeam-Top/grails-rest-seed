package top.dteam.earth.backend

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.OffsetDateTime

@Integration
@Rollback
class ExampleServiceSpec extends Specification {

    ExampleService exampleService

    void 'string2LocalDateTime 应该能支持多种LocalDateTime格式'() {
        // 支持的时间日期格式:
        // 2018-01-01 00:00:00
        // 2018-01-01 00:00:00.000
        // 2018-01-01T00:00:00
        // 2018-01-01T00:00:00.000
        expect:
        exampleService.string2LocalDateTime('2018-01-01 01:01:01').isEqual(LocalDateTime.parse('2018-01-01T01:01:01'))
        exampleService.string2LocalDateTime('2018-01-01 01:01:01.001').isEqual(LocalDateTime.parse('2018-01-01T01:01:01.001'))
        exampleService.string2LocalDateTime('2018-01-01T01:01:01').isEqual(LocalDateTime.parse('2018-01-01T01:01:01'))
        exampleService.string2LocalDateTime('2018-01-01T01:01:01.001').isEqual(LocalDateTime.parse('2018-01-01T01:01:01.001'))
    }

    void 'string2OffsetDateTime 应该支持多种OffsetDateTime格式'() {
        // 支持的时间日期格式:
        // 2018-01-01 00:00:00Z
        // 2018-01-01 00:00:00+08
        // 2018-01-01 00:00:00+0800
        // 2018-01-01 00:00:00+08:00
        // 2018-01-01 00:00:00.000Z
        // 2018-01-01 00:00:00.000+08
        // 2018-01-01 00:00:00.000+0800
        // 2018-01-01 00:00:00.000+08:00
        // 2018-01-01T00:00:00Z
        // 2018-01-01T00:00:00+08
        // 2018-01-01T00:00:00+0800
        // 2018-01-01T00:00:00+08:00
        // 2018-01-01T00:00:00.000Z
        // 2018-01-01T00:00:00.000+08
        // 2018-01-01T00:00:00.000+0800
        // 2018-01-01T00:00:00.000+08:00
        expect:
        exampleService.string2OffsetDateTime('2018-01-01 01:01:01Z').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01Z'))
        exampleService.string2OffsetDateTime('2018-01-01 01:01:01+08').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01+08:00'))
        exampleService.string2OffsetDateTime('2018-01-01 01:01:01+0800').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01+08:00'))
        exampleService.string2OffsetDateTime('2018-01-01 01:01:01+08:00').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01+08:00'))
        exampleService.string2OffsetDateTime('2018-01-01 01:01:01.001Z').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001Z'))
        exampleService.string2OffsetDateTime('2018-01-01 01:01:01.001+08').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001+08:00'))
        exampleService.string2OffsetDateTime('2018-01-01 01:01:01.001+0800').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001+08:00'))
        exampleService.string2OffsetDateTime('2018-01-01 01:01:01.001+08:00').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001+08:00'))
        exampleService.string2OffsetDateTime('2018-01-01T01:01:01Z').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01Z'))
        exampleService.string2OffsetDateTime('2018-01-01T01:01:01+08').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01+08:00'))
        exampleService.string2OffsetDateTime('2018-01-01T01:01:01+0800').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01+08:00'))
        exampleService.string2OffsetDateTime('2018-01-01T01:01:01+08:00').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01+08:00'))
        exampleService.string2OffsetDateTime('2018-01-01T01:01:01.001Z').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001Z'))
        exampleService.string2OffsetDateTime('2018-01-01T01:01:01.001+08').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001+08:00'))
        exampleService.string2OffsetDateTime('2018-01-01T01:01:01.001+0800').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001+08:00'))
        exampleService.string2OffsetDateTime('2018-01-01T01:01:01.001+08:00').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001+08:00'))
    }

}
