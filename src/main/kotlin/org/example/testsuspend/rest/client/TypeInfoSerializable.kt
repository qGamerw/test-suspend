package org.example.testsuspend.rest.client

import io.ktor.util.reflect.TypeInfo
import kotlin.reflect.KClass

class TypeInfoSerializable<Request : java.io.Serializable, Response : Any>(
    requestType: KClass<Request>,
    responseType: KClass<Response>,
) : Serializable<Request, Response> {

    override val requestTypeInfo = TypeInfo(requestType, requestType.java, null)
    override val responseTypeInfo = TypeInfo(responseType, responseType.java, null)
}
