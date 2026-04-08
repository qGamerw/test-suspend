package org.example.testsuspend.rest.impl

import org.example.testsuspend.rest.HttpRestClientDelegate
import org.example.testsuspend.rest.dto.Result
import org.slf4j.LoggerFactory

class IntegrationLoggerHttpRestClientDelegate<Request, Response>(
    private val delegate: HttpRestClientDelegate<Request, Response>,
) : HttpRestClientDelegate<Request, Response> {

    override suspend fun postCall(request: Request): Result<Response> {
        logger.info("Executing POST request: {}", request)

        val response = delegate.postCall(request)

        logger.info("Received POST response: {}", response)

        return response
    }

}

val logger by lazy { LoggerFactory.getLogger(IntegrationLoggerHttpRestClientDelegate::class.java) }
