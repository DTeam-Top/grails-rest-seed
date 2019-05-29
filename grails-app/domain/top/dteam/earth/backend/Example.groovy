package top.dteam.earth.backend

import net.kaleidos.hibernate.usertype.ArrayType
import net.kaleidos.hibernate.usertype.JsonbMapType

import java.time.OffsetDateTime

class Example {

    Map kvPair
    String[] strings
    OffsetDateTime dateTimeField = OffsetDateTime.now()
    OffsetDateTime dateCreated

    static mapping = {
        kvPair comment: 'Jsonb示例', type: JsonbMapType
        strings comment: '数组示例', type: ArrayType, params: [type: String]
        dateTimeField comment: '带时区的日期时间字段示例', sqlType: 'timestamptz'
        dateCreated comment: '创建时间', sqlType: 'timestamptz'
    }

}
