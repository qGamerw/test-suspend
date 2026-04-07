package org.example.testsuspend.rest.client.impl

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.util.reflect.TypeInfo
import java.util.concurrent.atomic.AtomicReference
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.client.RestClientFactory
import org.example.testsuspend.rest.configuration.HttpConfiguration
import org.example.testsuspend.rest.dto.ApiError
import org.example.testsuspend.rest.dto.Result

class KtorRestClient<Request, Response>(
    private val httpConfiguration: HttpConfiguration,
    private val restClientFactory: RestClientFactory,
    private val requestTypeInfo: TypeInfo,
    private val responseTypeInfo: TypeInfo,
) : RestClient<Request, Response>, AutoCloseable {

    private val clientRef = AtomicReference(restClientFactory.create(httpConfiguration))

    override suspend fun getCall(request: Request): Result<Response> {
        val response = clientRef.get().get(httpConfiguration.url) {
            url {
                setBody(request, requestTypeInfo)
            }
        }

        return if (response.status == HttpStatusCode.OK) {
            Result.success(response.body(responseTypeInfo))
        } else {
            Result.failure(
                ApiError(
                    response.status.value,
                    response.status.description,
                    "Error during GET call"
                )
            )
        }
    }

    override fun close() {
        clientRef.get()?.close()
    }
}
