package top.dteam.earth.backend.operation

import com.epages.restdocs.apispec.ResourceSnippetParameters
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.junit.Rule
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.FieldDescriptor
import spock.lang.Specification
import top.dteam.earth.backend.user.User
import top.dteam.earth.backend.utils.TestUtils

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName
import static com.epages.restdocs.apispec.ResourceDocumentation.resource
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document
import static io.restassured.RestAssured.given
import static org.springframework.http.HttpHeaders.AUTHORIZATION
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration

// TODO: 写一个BaseApiDocSpec模板类或trait，自动初始化REST Assured
@Integration
@Rollback
class AliyunOSSApiDocSpec extends Specification {

    @Rule
    JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation()

    private RequestSpecification documentationSpec

    FieldDescriptor[] response = [
            fieldWithPath("accessKeyId").description("OSS的access key id")
            , fieldWithPath("policy").description("OSS的权限矩阵")
            , fieldWithPath("signature").description("OSS认证成功后的签名")
            , fieldWithPath("dir").description("有权限上传的目录")
            , fieldWithPath("host").description("OSS访问主机")
            , fieldWithPath("expire").description("授权过期时间")
            , fieldWithPath("cdnUrl").description("用于外部访问的CDN URL(可空)").optional()
    ]

    void setup() {
        this.documentationSpec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors().withResponseDefaults(prettyPrint()))
                .setPort(serverPort)
                .build()
        TestUtils.initEnv()
    }

    void cleanup() {
        TestUtils.clearEnv()
    }

    void '所有登录用户均有权限获取上传权限 - apidoc'() {
        setup:
        User.withNewTransaction {
            TestUtils.createUser('ROLE_ADMIN', '13500000001')
        }
        String jwt = TestUtils.login(serverPort, '13500000001', '13500000001')

        expect:
        given(this.documentationSpec).accept(ContentType.JSON)
                .filter(document("getUploadAuthority"
                        , resource(ResourceSnippetParameters.builder()
                        .summary('获取阿里云OSS上传权限')
                        .description('获取阿里云OSS的上传权限的签名字符串及目录等配置，具体用法请参考[服务端签名后直传](https://help.aliyun.com/document_detail/31926.html)')
                        .responseFields(response)
                        .requestHeaders(headerWithName(AUTHORIZATION).description('JWT'))
                        .tags('operation')
                        .build())))
                .header(AUTHORIZATION, "Bearer ${jwt}")
                .when().get("/api/getUploadAuthority")
                .then().assertThat().statusCode(200)
    }
}
