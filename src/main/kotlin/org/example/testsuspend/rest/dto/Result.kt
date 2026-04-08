package org.example.testsuspend.rest.dto

sealed interface Result<out Response> {

    data class Success<T>(
        val requestId: String,
        val statusCode: Int,
        val data: T,
    ) : Result<T>

    data class Failure(
        val error: ApiError,
    ) : Result<Nothing>
}

data class ApiError(
    val code: String,
    val message: String,
    val requestId: String,
    val statusCode: Int? = null,
    val responseBody: String? = null,
    val headers: Map<String, List<String>> = emptyMap(),
)
