package org.example.testsuspend.rest.client

import org.example.testsuspend.rest.logging.RestClientLogWriter

class LoggingRestClient<Rq : Any, Rs : Any>(
    private val operationName: String,
    private val delegate: RestClient<Rq, Rs>,
    private val logWriter: RestClientLogWriter,
    private val requestIdProvider: (Rq) -> String,
) : RestClient<Rq, Rs> {

    override suspend fun call(request: Rq): Rs {
        val startedAt = System.nanoTime()
        val requestId = requestIdProvider(request)
        logWriter.logRequest(operationName, requestId, request)

        return try {
            val response = delegate.call(request)
            logWriter.logResponse(operationName, requestId, response, elapsedMillis(startedAt))
            response
        } catch (throwable: Throwable) {
            logWriter.logFailure(operationName, requestId, elapsedMillis(startedAt), throwable)
            throw throwable
        }
    }

    private fun elapsedMillis(startedAt: Long): Long = (System.nanoTime() - startedAt) / 1_000_000
}
