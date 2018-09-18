package top.dteam.earth.backend.example

import grails.databinding.converters.ValueConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

import java.time.LocalDateTime

class Example {

    @Autowired
    @Qualifier('localDateTimeValueConverter')
    ValueConverter localDateTimeValueConverter

    LocalDateTime string2LocalDateTime(String startTime) {
        localDateTimeValueConverter.convert(startTime)
    }
}
