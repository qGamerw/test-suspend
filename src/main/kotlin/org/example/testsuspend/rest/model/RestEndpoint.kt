package org.example.testsuspend.rest.model

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod

/**
 * Описывает неизменяемые параметры вызова внешнего REST-метода.
 */
data class RestEndpoint(
    val operationName: String,
    val method: HttpMethod,
    val url: String,
    val headers: Map<String, String> = emptyMap(),
    val queryParams: Map<String, List<String>> = emptyMap(),
    val contentType: ContentType = ContentType.Application.Json,
    val timeoutMillis: Long? = null,
)
