package top.dteam.earth.backend.user

import com.epages.restdocs.apispec.FieldDescriptors
import com.epages.restdocs.apispec.ResourceSnippetParameters
import grails.core.GrailsApplication
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import io.restassured.http.ContentType
import org.springframework.restdocs.payload.JsonFieldType
import top.dteam.earth.backend.utils.BaseApiDocSpec
import top.dteam.earth.backend.utils.TestUtils

import static com.epages.restdocs.apispec.ResourceDocumentation.resource
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document
import static io.restassured.RestAssured.given
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath

@Integration
@Rollback
class UserApiDocSpec extends BaseApiDocSpec {

    GrailsApplication grailsApplication

    private FieldDescriptors loginResponseFields = new FieldDescriptors([
            fieldWithPath('username').description('用户名')
            , fieldWithPath('roles').description('用户所拥有的角色名').type(JsonFieldType.ARRAY)
            , fieldWithPath('userId').description('用户id')
            , fieldWithPath('displayName').description('用户昵称')
            , fieldWithPath('token_type').description('token类型')
            , fieldWithPath('access_token').description('JWT')
            , fieldWithPath('expires_in').description('过期时间')
            , fieldWithPath('refresh_token').description('用于请求续期接口的token')
    ])

    private FieldDescriptors loginRequestFields = new FieldDescriptors([
            fieldWithPath('username').description('用户名')
            , fieldWithPath('password').description('密码')
    ])

    void setup() {
        TestUtils.initEnv()
    }

    void cleanup() {
        TestUtils.clearEnv()
    }

    void '用户登录接口 - apidoc'() {
        setup:
        String loginEndpointUrl = grailsApplication.config.grails.plugin.springsecurity.rest.login.endpointUrl

        expect:
        given(this.documentationSpec).accept(ContentType.JSON).contentType(ContentType.JSON)
                .filter(document("login"
                        , resource(ResourceSnippetParameters.builder()
                        .summary('用户登录')
                        .description('用户使用用户名和密码登录的接口，返回JWT')
                        .requestFields(loginRequestFields)
                        .responseFields(loginResponseFields)
                        .tags('user')
                        .build())))
                .body([username: '13500000000', password: '13500000000'])
                .when().post(loginEndpointUrl)
                .then().assertThat().statusCode(200)
    }

}
