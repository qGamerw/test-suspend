package org.example.testsuspend.rest.logging

interface RestClientLogWriter {
    fun logRequest(operationName: String, requestId: String, request: Any)

    fun logResponse(operationName: String, requestId: String, response: Any, durationMillis: Long)

    fun logFailure(operationName: String, requestId: String, durationMillis: Long, throwable: Throwable)
}
