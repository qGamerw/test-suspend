package org.example.testsuspend.rest.logging

import org.example.testsuspend.rest.model.RawRestResponse
import org.example.testsuspend.rest.model.RestEndpoint
import org.example.testsuspend.rest.model.RestRequest
import org.slf4j.LoggerFactory

/**
 * Реализация логера по умолчанию, которая пишет структурированные события через SLF4J.
 */
class Slf4jRestClientLogWriter : RestClientLogWriter {

    private val logger = LoggerFactory.getLogger(Slf4jRestClientLogWriter::class.java)
    private val maskedHeaders = setOf("authorization", "cookie", "set-cookie", "x-api-key")
    private val maxBodyLength = 2_000

    override fun logRequest(endpoint: RestEndpoint, request: RestRequest) {
        logger.info(
            "event=rest.request.sent operation={} method={} url={} headers={} query={} body={}",
            endpoint.operationName,
            endpoint.method.value,
            endpoint.url,
            maskHeaders(endpoint.headers),
            endpoint.queryParams,
            trimBody(request.body),
        )
    }

    override fun logResponse(endpoint: RestEndpoint, request: RestRequest, response: RawRestResponse, durationMillis: Long) {
        logger.info(
            "event=rest.response.received operation={} method={} url={} status={} duration_ms={} headers={} body={}",
            endpoint.operationName,
            endpoint.method.value,
            endpoint.url,
            response.status,
            durationMillis,
            response.headers,
            trimBody(response.body),
        )
    }

    override fun logFailure(endpoint: RestEndpoint, request: RestRequest, durationMillis: Long, throwable: Throwable) {
        logger.warn(
            "event=rest.request.failed operation={} method={} url={} duration_ms={} error_type={} error_message={}",
            endpoint.operationName,
            endpoint.method.value,
            endpoint.url,
            durationMillis,
            throwable::class.simpleName,
            throwable.message,
        )
    }

    override fun logMetric(event: RestMetricEvent) {
        logger.info(
            "event=rest.metrics metric=rest.client.request operation={} method={} status={} outcome={} duration_ms={} count={}",
            event.operationName,
            event.method,
            event.status,
            event.outcome,
            event.durationMillis,
            event.count,
        )
    }

    private fun maskHeaders(headers: Map<String, String>): Map<String, String> =
        headers.mapValues { (key, value) ->
            if (maskedHeaders.contains(key.lowercase())) "***" else value
        }

    private fun trimBody(body: String?): String? {
        if (body == null) {
            return null
        }
        return if (body.length <= maxBodyLength) body else body.take(maxBodyLength) + "..."
    }
}
