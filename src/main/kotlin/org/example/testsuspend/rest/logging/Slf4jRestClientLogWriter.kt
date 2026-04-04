package org.example.testsuspend.rest.logging

import org.slf4j.LoggerFactory

class Slf4jRestClientLogWriter : RestClientLogWriter {

    private val logger = LoggerFactory.getLogger(Slf4jRestClientLogWriter::class.java)

    override fun logRequest(operationName: String, requestId: String, request: Any) {
        logger.info("rest.request operation={} requestId={}", operationName, requestId)

        if (logger.isDebugEnabled) {
            logger.debug("rest.request.payload operation={} requestId={} payload={}", operationName, requestId, request)
        }
    }

    override fun logResponse(operationName: String, requestId: String, response: Any, durationMillis: Long) {
        logger.info(
            "rest.response operation={} requestId={} durationMs={}",
            operationName,
            requestId,
            durationMillis,
        )

        if (logger.isDebugEnabled) {
            logger.debug(
                "rest.response.payload operation={} requestId={} payload={}",
                operationName,
                requestId,
                response,
            )
        }
    }

    override fun logFailure(operationName: String, requestId: String, durationMillis: Long, throwable: Throwable) {
        logger.warn(
            "rest.failure operation={} requestId={} durationMs={} message={}",
            operationName,
            requestId,
            durationMillis,
            throwable.message,
            throwable,
        )
    }
}
