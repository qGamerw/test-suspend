package org.example.testsuspend.rest.client

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

open class RestClientException(
    message: String,
    val url: String,
    val method: HttpMethod,
    val requestId: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

class RestClientHttpException(
    url: String,
    method: HttpMethod,
    requestId: String,
    val statusCode: HttpStatusCode,
    val responseBody: String,
) : RestClientException(
    message = "HTTP ${statusCode.value} from $url",
    url = url,
    method = method,
    requestId = requestId,
)

class RestClientTimeoutException(
    url: String,
    method: HttpMethod,
    requestId: String,
    cause: Throwable,
) : RestClientException(
    message = "Timeout while calling $url",
    url = url,
    method = method,
    requestId = requestId,
    cause = cause,
)

class RestClientTransportException(
    url: String,
    method: HttpMethod,
    requestId: String,
    cause: Throwable,
) : RestClientException(
    message = "Transport error while calling $url",
    url = url,
    method = method,
    requestId = requestId,
    cause = cause,
)

class RestClientSerializationException(
    url: String,
    method: HttpMethod,
    requestId: String,
    cause: Throwable,
) : RestClientException(
    message = "Serialization error while calling $url",
    url = url,
    method = method,
    requestId = requestId,
    cause = cause,
)
