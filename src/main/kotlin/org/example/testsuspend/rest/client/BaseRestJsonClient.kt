package org.example.testsuspend.rest.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.withTimeout
import org.example.testsuspend.rest.exception.TransportException
import org.example.testsuspend.rest.model.RawRestResponse
import org.example.testsuspend.rest.model.RestEndpoint
import org.example.testsuspend.rest.model.RestRequest

/**
 * Отправляет HTTP-запросы через Ktor без логирования, метрик и маппинга.
 */
class BaseRestJsonClient(
    private val httpClient: HttpClient,
    private val endpoint: RestEndpoint,
) : RawRestJsonClient {

    override suspend fun execute(request: RestRequest): RawRestResponse {
        return try {
            val response = executeRequest(request)

            RawRestResponse(
                status = response.status.value,
                headers = response.headers.toMap(),
                body = response.body(),
            )
        } catch (exception: Exception) {
            throw TransportException(
                message = "Failed to execute ${endpoint.method.value} ${endpoint.url}",
                cause = exception,
            )
        }
    }

    private suspend fun executeRequest(request: RestRequest) =
        endpoint.timeoutMillis?.let { timeoutMillis ->
            withTimeout(timeoutMillis) {
                performRequest(request)
            }
        } ?: performRequest(request)

    private suspend fun performRequest(request: RestRequest) =
        httpClient.request(endpoint.url) {
            this.method = endpoint.method
            endpoint.queryParams.forEach { (key, values) ->
                values.forEach { value -> parameter(key, value) }
            }
            endpoint.headers.forEach { (key, value) -> header(key, value) }
            if (request.body != null) {
                contentType(endpoint.contentType)
                if (!endpoint.headers.containsKey(HttpHeaders.ContentType)) {
                    header(HttpHeaders.ContentType, endpoint.contentType.toString())
                }
                setBody(request.body)
            }
        }

    private fun Headers.toMap(): Map<String, List<String>> =
        entries().associate { entry -> entry.key to entry.value }
}
