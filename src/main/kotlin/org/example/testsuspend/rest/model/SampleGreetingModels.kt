package org.example.testsuspend.rest.model

import kotlinx.serialization.Serializable

/** Пример DTO тела запроса во внешнее API. */
@Serializable
data class GreetingRequest(
    val name: String,
)

/** Пример DTO для успешного ответа внешнего API. */
@Serializable
data class GreetingResponse(
    val message: String,
)

/** Пример DTO для ошибочного ответа внешнего API. */
@Serializable
data class ApiErrorResponse(
    val code: String,
    val message: String,
)
