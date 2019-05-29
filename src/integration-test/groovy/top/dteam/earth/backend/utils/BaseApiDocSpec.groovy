package top.dteam.earth.backend.utils

import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.junit.Before
import org.junit.Rule
import org.springframework.restdocs.JUnitRestDocumentation
import spock.lang.Specification

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

abstract class BaseApiDocSpec extends Specification {

    @Rule
    JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation()

    RequestSpecification documentationSpec

    @Before
    void apiSpec() {
        this.documentationSpec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(this.restDocumentation)
                        .operationPreprocessors().withResponseDefaults(prettyPrint()))
                .setPort(this.serverPort)
                .build()
    }

}
