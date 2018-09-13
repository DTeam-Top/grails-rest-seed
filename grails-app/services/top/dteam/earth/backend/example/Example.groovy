package top.dteam.earth.backend.example

import java.time.LocalDateTime

class Example {

    @Autowired
    @Qualifier('localDateTimeValueConverter')
    ValueConverter localDateTimeValueConverter

    LocalDateTime string2LocalDateTime(String startTime) {
        localDateTimeValueConverter.convert(startTime)
    }
}
