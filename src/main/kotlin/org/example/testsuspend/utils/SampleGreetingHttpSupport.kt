package org.example.testsuspend.utils

import org.example.testsuspend.rest.model.RestCallResult
import org.example.testsuspend.sample.service.ApiErrorResponse
import org.example.testsuspend.sample.service.GreetingResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Тело CRUD-запроса для sample endpoint'ов.
 */
data class SampleGreetingPayload(
    val name: String,
)

/**
 * Преобразует результат вызова sample-сервиса в HTTP-ответ.
 */
fun RestCallResult<GreetingResponse, ApiErrorResponse>.toResponse(): ResponseEntity<Any> =
    when (this) {
        is RestCallResult.Success -> ResponseEntity.ok(body)
        is RestCallResult.Failure -> ResponseEntity.status(HttpStatus.valueOf(status)).body(error ?: rawBody)
    }
