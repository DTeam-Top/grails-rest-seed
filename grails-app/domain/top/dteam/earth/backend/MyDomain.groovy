package top.dteam.earth.backend

import net.kaleidos.hibernate.usertype.ArrayType
import net.kaleidos.hibernate.usertype.JsonbMapType

import java.time.LocalDateTime

class MyDomain {

    Map kvPair
    String[] strings
    LocalDateTime dateCreated

    static constraints = {
        kvPair comment: 'Jsonb示例', type: JsonbMapType
        strings comment: '数组示例', type: ArrayType, params: [type: String]
    }
}
