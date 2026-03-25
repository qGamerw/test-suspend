package org.example.testsuspend.rest.client.decorator

import kotlin.time.TimeSource
import org.example.testsuspend.rest.client.RawRestJsonClient
import org.example.testsuspend.rest.logging.RestClientLogWriter
import org.example.testsuspend.rest.model.RawRestResponse
import org.example.testsuspend.rest.model.RestEndpoint
import org.example.testsuspend.rest.model.RestRequest

/**
 * Логирует запрос, ответ и ошибки вокруг сырого REST-вызова.
 */
class LoggingRestJsonClientDecorator(
    delegate: RawRestJsonClient,
    private val logWriter: RestClientLogWriter,
    private val endpoint: RestEndpoint,
) : RestJsonClientDecorator(delegate) {

    override suspend fun execute(request: RestRequest): RawRestResponse {
        logWriter.logRequest(endpoint, request)
        val startedAt = TimeSource.Monotonic.markNow()

        return try {
            delegate.execute(request).also { response ->
                logWriter.logResponse(endpoint, request, response, startedAt.elapsedNow().inWholeMilliseconds)
            }
        } catch (exception: Exception) {
            logWriter.logFailure(endpoint, request, startedAt.elapsedNow().inWholeMilliseconds, exception)
            throw exception
        }
    }
}
