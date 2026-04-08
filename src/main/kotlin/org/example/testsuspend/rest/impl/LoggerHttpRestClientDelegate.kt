package org.example.testsuspend.rest.impl

import org.example.testsuspend.rest.HttpRestClientDelegate
import org.example.testsuspend.rest.dto.Result
import org.slf4j.LoggerFactory

class LoggerHttpRestClientDelegate<Request, Response>(
    private val delegate: HttpRestClientDelegate<Request, Response>,
) : HttpRestClientDelegate<Request, Response> {

    override suspend fun postCall(request: Request): Result<Response> {
        logger.info("Executing GET request: {}", request)

        val response = delegate.postCall(request)

        logger.info("Received GET response: {}", response)

        return response
    }

}

val logger by lazy { LoggerFactory.getLogger(LoggerHttpRestClientDelegate::class.java) }
