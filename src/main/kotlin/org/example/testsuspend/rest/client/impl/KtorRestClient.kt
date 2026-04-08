package org.example.testsuspend.rest.client.impl

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.util.reflect.TypeInfo
import java.util.concurrent.atomic.AtomicReference
import org.example.testsuspend.rest.client.Reconfigurable
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.client.RestClientFactory
import org.example.testsuspend.rest.dto.Result
import org.example.testsuspend.rest.dto.properties.HttpConfiguration

class KtorRestClient<Request, Response>(
    private val httpConfiguration: HttpConfiguration,
    private val restClientFactory: RestClientFactory,
    private val requestTypeInfo: TypeInfo,
    private val responseTypeInfo: TypeInfo,
) : RestClient<Request, Response>, AutoCloseable, Reconfigurable {

    private val clientRef = AtomicReference(restClientFactory.create(httpConfiguration))

    override suspend fun postCall(request: Request): Result<Response> {
        var response: HttpResponse? = null
        try {
            response = clientRef.get().post(httpConfiguration.path) {
                url {
                    headers
                }
                setBody(request, requestTypeInfo)
            }
            return Result.success(response, response.body(responseTypeInfo))
        } catch (e: Exception) {
            return Result.failure(
                response,
                e
            )
        }
    }

    override fun reconfigure() {
        val oldClient = clientRef.getAndSet(restClientFactory.create(httpConfiguration))
        oldClient.close()
    }

    override fun close() {
        clientRef.get()?.close()
    }
}
