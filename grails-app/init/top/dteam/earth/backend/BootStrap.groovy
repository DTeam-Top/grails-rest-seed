package top.dteam.earth.backend

import groovy.util.logging.Slf4j
import org.springframework.boot.info.GitProperties

@Slf4j
class BootStrap {
    GitProperties gitProperties

    def init = { servletContext ->
        log.info("Application running at commit: ${gitProperties.shortCommitId}, branch: ${gitProperties.branch}, commit time: ${gitProperties.commitTime}, build time: ${gitProperties.getDate('build.time')}")
    }
    def destroy = {
    }
}
