package org.example.testsuspend.rest.client

import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import java.io.Closeable
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CancellationException
import org.example.testsuspend.rest.config.HttpClientSettings
import org.example.testsuspend.rest.config.HttpConfiguration
import org.example.testsuspend.rest.config.KtorHttpClientFactory
import io.ktor.client.HttpClient as KtorRawHttpClient

class KtorRestClient<Request : java.io.Serializable, Response : Any>(
    val configuration: HttpConfiguration,
    private val httpClientFactory: KtorHttpClientFactory,
    private val serializer: Serializable<Request, Response>,
    private val requestCustomizer: (HttpRequestBuilder, RequestDescription<Request>) -> Unit = { _, _ -> },
    private val integrationInfo: IntegrationInfo<Request>,
) : HttpClient<Request, Response>, Closeable, Restartable {

    private val clientRef = AtomicReference(createClient(configuration.clientSettings()))

    override suspend fun call(request: Request): Response {
        val requestDescription = RequestDescription(
            request = request,
            url = configuration.url,
            method = configuration.method,
            integrationInfo = integrationInfo,
        )
        val requestId = integrationInfo.requestIdProvider(request)

        try {
            val response = clientRef.get().request(configuration.url) {
                method = configuration.method
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                header(configuration.requestIdHeaderName, requestId)
                integrationInfo.customHeadersProvider(request).forEach { (key, value) ->
                    header(key, value)
                }
                requestCustomizer(this, requestDescription)
                setBody(request, serializer.requestTypeInfo)
            }

            ensureSuccess(response, requestId)

            return try {
                response.body(serializer.responseTypeInfo)
            } catch (throwable: Throwable) {
                throw mapSerializationException(requestId, throwable)
            }
        } catch (throwable: Throwable) {
            throw mapCallException(requestId, throwable)
        }
    }

    override fun restart() {
        val newClient = createClient(configuration.clientSettings())
        val oldClient = clientRef.getAndSet(newClient)
        oldClient.close()
    }

    fun reconfigure(settings: HttpClientSettings) {
        configuration.reconfigure(settings)
        restart()
    }

    override fun close() {
        clientRef.get().close()
    }

    private suspend fun ensureSuccess(response: HttpResponse, requestId: String) {
        if (response.status.isSuccess()) {
            return
        }

        throw RestClientHttpException(
            url = configuration.url,
            method = configuration.method,
            requestId = requestId,
            statusCode = response.status,
            responseBody = response.bodyAsText(),
        )
    }

    private fun mapCallException(requestId: String, throwable: Throwable): Throwable = when (throwable) {
        is CancellationException -> throwable
        is RestClientException -> throwable
        is HttpRequestTimeoutException -> RestClientTimeoutException(configuration.url, configuration.method, requestId, throwable)
        is IOException -> RestClientTransportException(configuration.url, configuration.method, requestId, throwable)
        else -> throwable
    }

    private fun mapSerializationException(requestId: String, throwable: Throwable): RestClientSerializationException =
        RestClientSerializationException(configuration.url, configuration.method, requestId, throwable)

    private fun createClient(settings: HttpClientSettings): KtorRawHttpClient = httpClientFactory.create(settings)
}
