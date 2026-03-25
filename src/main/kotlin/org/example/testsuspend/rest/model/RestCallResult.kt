package org.example.testsuspend.rest.model

/**
 * Типизированный результат REST-вызова после маппинга ответа.
 */
sealed interface RestCallResult<out S, out E> {
    /** Успешный ответ с уже преобразованным телом. */
    data class Success<S>(
        val status: Int,
        val body: S,
        val headers: Map<String, List<String>> = emptyMap(),
    ) : RestCallResult<S, Nothing>

    /** Неуспешный ответ с преобразованным телом ошибки, если оно присутствует. */
    data class Failure<E>(
        val status: Int,
        val error: E?,
        val rawBody: String?,
        val headers: Map<String, List<String>> = emptyMap(),
    ) : RestCallResult<Nothing, E>
}
