package org.example.testsuspend.rest.client

import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.CancellationException
import org.example.testsuspend.rest.config.HttpClientProvider
import java.io.IOException
import kotlin.reflect.KClass

class KtorRestClient<Rq : RequestInfo, Rs : Any>(
    private val httpClientProvider: HttpClientProvider,
    private val url: String,
    requestType: KClass<Rq>,
    responseType: KClass<Rs>,
    private val requestIdHeaderName: String,
    private val httpMethod: HttpMethod = HttpMethod.Post,
    private val configureRequest: HttpRequestBuilder.() -> Unit = {},
) : RestClient<Rq, Rs> {

    // TypeInfo считаем один раз, чтобы не собирать его на каждый запрос.
    private val requestTypeInfo = TypeInfo(requestType, requestType.java, null)
    private val responseTypeInfo = TypeInfo(responseType, responseType.java, null)

    override suspend fun call(request: Rq): Rs {
        try {
            // Берём актуальный HttpClient из provider, чтобы поддерживать runtime-реконфигурацию.
            val response = httpClientProvider.get().request(url) {
                method = httpMethod
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                header(requestIdHeaderName, request.requestId)
                request.customHeaders.forEach { (key, value) ->
                    header(key, value)
                }
                setBody(request, requestTypeInfo)
                configureRequest()
            }

            ensureSuccess(response, request.requestId)

            return try {
                response.body(responseTypeInfo)
            } catch (throwable: Throwable) {
                throw mapSerializationException(request.requestId, throwable)
            }
        } catch (throwable: Throwable) {
            throw mapCallException(request.requestId, throwable)
        }
    }

    private suspend fun ensureSuccess(response: HttpResponse, requestId: String) {
        if (response.status.isSuccess()) {
            return
        }

        throw RestClientHttpException(
            url = url,
            method = httpMethod,
            requestId = requestId,
            statusCode = response.status,
            responseBody = response.bodyAsText(),
        )
    }

    private fun mapCallException(requestId: String, throwable: Throwable): Throwable = when (throwable) {
        is CancellationException -> throwable
        is RestClientException -> throwable
        is HttpRequestTimeoutException -> RestClientTimeoutException(url, httpMethod, requestId, throwable)
        is IOException -> RestClientTransportException(url, httpMethod, requestId, throwable)
        else -> throwable
    }

    private fun mapSerializationException(requestId: String, throwable: Throwable): RestClientSerializationException =
        RestClientSerializationException(url, httpMethod, requestId, throwable)
}
