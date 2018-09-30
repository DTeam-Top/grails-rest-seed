import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter

import java.nio.charset.Charset

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')

        pattern =
                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                        '%clr(%5p) ' + // Log level
                        '%clr(%-40.40logger{39}){cyan} %clr(:){faint} ' + // Logger
                        '%m%n%wex' // Message
    }
}

appender("R_SQL", RollingFileAppender) {
    file = "sql.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d %-5level %logger{36} - %msg%n"
    }
    rollingPolicy(FixedWindowRollingPolicy) {
        fileNamePattern = "sql.log.%i"
        minIndex = 1
        maxIndex = 5
    }
    triggeringPolicy(SizeBasedTriggeringPolicy) {
        maxFileSize = "10MB"
    }
}

appender("R", RollingFileAppender) {
    file = "earth.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d %-5level %logger{36} - %msg%n"
    }
    rollingPolicy(FixedWindowRollingPolicy) {
        fileNamePattern = "earth.log.%i"
        minIndex = 1
        maxIndex = 5
    }
    triggeringPolicy(SizeBasedTriggeringPolicy) {
        maxFileSize = "10MB"
    }
}

def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir != null) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
}

final String LOG_LEVEL = System.getenv('LOG_LEVEL')
final boolean DEBUG_SQL = System.getenv('DEBUG_SQL') as boolean

root(valueOf(LOG_LEVEL), ['STDOUT', 'R'])
logger('org', WARN)
logger('grails.plugin.springsecurity', WARN)
logger('grails', WARN)
logger('com.zaxxer', WARN)
logger('liquibase', WARN)
logger('ctory', WARN)
logger('liquibase-hibernate', WARN)
logger('org.springframework.security', WARN)
logger('org.hibernate.orm.deprecation', ERROR)

if (DEBUG_SQL) {
    logger('org.hibernate.SQL', DEBUG, ['R_SQL'])
    logger('org.hibernate.type', TRACE, ['R_SQL'])
} else {
    logger('org.hibernate.SQL', WARN)
}
