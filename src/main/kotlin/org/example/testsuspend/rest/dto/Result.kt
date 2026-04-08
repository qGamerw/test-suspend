package org.example.testsuspend.rest.dto

import io.ktor.client.statement.HttpResponse

data class Result<Response>(
    val data: Response? = null,
    val response: HttpResponse? = null,
    val error: Exception? = null,
) {

    companion object {
        @JvmStatic
        fun <T> success(response: HttpResponse, data: T): Result<T> = Result(response = response, data = data)

        @JvmStatic
        fun <T> failure(response: HttpResponse?, error: Exception): Result<T> =
            Result(response = response, error = error)
    }
}
