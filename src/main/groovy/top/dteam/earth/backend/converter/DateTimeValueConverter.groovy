package top.dteam.earth.backend.converter

import grails.databinding.converters.ValueConverter
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

/**
 * 同时支持LocalDateTime和OffsetDateTime value converter，统一输出OffsetDateTime
 */
@CompileStatic
class DateTimeValueConverter implements ValueConverter {

    final Class targetType = OffsetDateTime

    @Autowired
    ValueConverter localDateTimeValueConverter

    @Autowired
    ValueConverter offsetDateTimeValueConverter

    @Override
    boolean canConvert(Object value) {
        value instanceof String ||
                value instanceof LocalDateTime ||
                value instanceof OffsetDateTime
    }

    @Override
    OffsetDateTime convert(Object value) {
        if (value instanceof OffsetDateTime) {
            return value
        }

        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).atOffset(OffsetDateTime.now().offset)
        }

        try {
            return offsetDateTimeValueConverter.convert(value) as OffsetDateTime
        } catch (DateTimeParseException e) {
            LocalDateTime localDateTime = localDateTimeValueConverter.convert(value) as LocalDateTime
            return localDateTime.atOffset(OffsetDateTime.now().offset)
        }
    }

}
