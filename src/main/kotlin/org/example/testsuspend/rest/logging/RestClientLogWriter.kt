package org.example.testsuspend.rest.logging

import org.example.testsuspend.rest.model.RawRestResponse
import org.example.testsuspend.rest.model.RestEndpoint
import org.example.testsuspend.rest.model.RestRequest

/**
 * Записывает структурированные лог-события для запросов, ответов, ошибок и метрик.
 */
interface RestClientLogWriter {
    fun logRequest(endpoint: RestEndpoint, request: RestRequest)
    fun logResponse(endpoint: RestEndpoint, request: RestRequest, response: RawRestResponse, durationMillis: Long)
    fun logFailure(endpoint: RestEndpoint, request: RestRequest, durationMillis: Long, throwable: Throwable)
    fun logMetric(event: RestMetricEvent)
}
