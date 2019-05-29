package top.dteam.earth.backend.converter

import grails.testing.mixin.integration.Integration
import spock.lang.Specification

import java.time.OffsetDateTime

@Integration
class DateTimeValueConverterSpec extends Specification {

    DateTimeValueConverter dateTimeValueConverter

    void 'DateTimeValueConverter应该能把所有支持的时间日期类型都转换成OffsetDateTime'() {
        setup:
        TimeZone defaultTimezone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone('UTC'))

        expect:
        dateTimeValueConverter.convert('2018-01-01 01:01:01').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01Z'))
        dateTimeValueConverter.convert('2018-01-01 01:01:01.001').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001Z'))
        dateTimeValueConverter.convert('2018-01-01T01:01:01').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01Z'))
        dateTimeValueConverter.convert('2018-01-01T01:01:01.001').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001Z'))
        dateTimeValueConverter.convert('2018-01-01 01:01:01Z').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01Z'))
        dateTimeValueConverter.convert('2018-01-01 01:01:01+08').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01+08:00'))
        dateTimeValueConverter.convert('2018-01-01 01:01:01+0800').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01+08:00'))
        dateTimeValueConverter.convert('2018-01-01 01:01:01+08:00').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01+08:00'))
        dateTimeValueConverter.convert('2018-01-01 01:01:01.001Z').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001Z'))
        dateTimeValueConverter.convert('2018-01-01 01:01:01.001+08').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001+08:00'))
        dateTimeValueConverter.convert('2018-01-01 01:01:01.001+0800').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001+08:00'))
        dateTimeValueConverter.convert('2018-01-01 01:01:01.001+08:00').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001+08:00'))
        dateTimeValueConverter.convert('2018-01-01T01:01:01Z').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01Z'))
        dateTimeValueConverter.convert('2018-01-01T01:01:01+08').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01+08:00'))
        dateTimeValueConverter.convert('2018-01-01T01:01:01+0800').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01+08:00'))
        dateTimeValueConverter.convert('2018-01-01T01:01:01+08:00').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01+08:00'))
        dateTimeValueConverter.convert('2018-01-01T01:01:01.001Z').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001Z'))
        dateTimeValueConverter.convert('2018-01-01T01:01:01.001+08').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001+08:00'))
        dateTimeValueConverter.convert('2018-01-01T01:01:01.001+0800').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001+08:00'))
        dateTimeValueConverter.convert('2018-01-01T01:01:01.001+08:00').isEqual(OffsetDateTime.parse('2018-01-01T01:01:01.001+08:00'))

        cleanup:
        TimeZone.setDefault(defaultTimezone)
    }

}
