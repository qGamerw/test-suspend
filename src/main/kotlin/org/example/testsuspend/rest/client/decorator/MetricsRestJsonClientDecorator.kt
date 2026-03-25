package org.example.testsuspend.rest.client.decorator

import kotlin.time.TimeSource
import org.example.testsuspend.rest.client.RawRestJsonClient
import org.example.testsuspend.rest.logging.RestClientLogWriter
import org.example.testsuspend.rest.logging.RestMetricEvent
import org.example.testsuspend.rest.model.RawRestResponse
import org.example.testsuspend.rest.model.RestEndpoint
import org.example.testsuspend.rest.model.RestRequest

/**
 * Формирует события метрик для сырого REST-вызова через настроенный логер.
 */
class MetricsRestJsonClientDecorator(
    delegate: RawRestJsonClient,
    private val logWriter: RestClientLogWriter,
    private val endpoint: RestEndpoint,
) : RestJsonClientDecorator(delegate) {

    override suspend fun execute(request: RestRequest): RawRestResponse {
        val startedAt = TimeSource.Monotonic.markNow()

        return try {
            delegate.execute(request).also { response ->
                logWriter.logMetric(
                    RestMetricEvent(
                        operationName = endpoint.operationName,
                        method = endpoint.method.value,
                        status = response.status,
                        outcome = if (response.status in 200..299) "success" else "failure",
                        durationMillis = startedAt.elapsedNow().inWholeMilliseconds,
                    ),
                )
            }
        } catch (exception: Exception) {
            logWriter.logMetric(
                RestMetricEvent(
                    operationName = endpoint.operationName,
                    method = endpoint.method.value,
                    status = null,
                    outcome = "transport_error",
                    durationMillis = startedAt.elapsedNow().inWholeMilliseconds,
                ),
            )
            throw exception
        }
    }
}
