package org.example.testsuspend.rest.dto

data class Result<Response>(
    val data: Response? = null,
    val error: ApiError? = null,
) {

    companion object {
        @JvmStatic
        fun <T> success(data: T): Result<T> = Result(data = data)

        @JvmStatic
        fun <T> failure(error: ApiError): Result<T> = Result(error = error)
    }
}

data class ApiError(
    val statusCode: Int,
    val code: String,
    val message: String,
)
