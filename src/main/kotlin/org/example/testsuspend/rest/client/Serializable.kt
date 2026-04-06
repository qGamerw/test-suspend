package org.example.testsuspend.rest.client

import io.ktor.util.reflect.TypeInfo

// Контракт хранит type metadata для transport-слоя, а actual JSON делает Ktor/Jackson.
interface Serializable<Request : java.io.Serializable, Response : Any> {
    val requestTypeInfo: TypeInfo
    val responseTypeInfo: TypeInfo
}
