package org.example.testsuspend.rest.client.impl

import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.util.reflect.TypeInfo
import java.util.UUID
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.client.RestClientProvider
import org.example.testsuspend.rest.dto.ApiError
import org.example.testsuspend.rest.dto.Result

class KtorRestClient<Request, Response>(
    private val getPath: () -> String,
    private val restClientProvider: RestClientProvider,
    private val requestTypeInfo: TypeInfo,
    private val responseTypeInfo: TypeInfo,
) : RestClient<Request, Response> {

    override suspend fun postCall(request: Request): Result<Response> {
        val requestId = UUID.randomUUID().toString()
        var response: HttpResponse? = null

        try {
            response = restClientProvider.clientRef.get().post(getPath()) {
                header(REQUEST_ID_HEADER, requestId)
                setBody(request, requestTypeInfo)
            }

            if (!response.status.isSuccess()) {
                return Result.Failure(
                    ApiError(
                        code = "HTTP_${response.status.value}",
                        message = "Downstream request failed with status ${response.status.value}",
                        requestId = requestId,
                        statusCode = response.status.value,
                        responseBody = response.bodyAsText(),
                        headers = response.headers.entries().associate { it.key to it.value },
                    )
                )
            }

            return Result.Success(requestId, response.status.value, response.body(responseTypeInfo))
        } catch (e: Exception) {
            return Result.Failure(
                ApiError(
                    code = e::class.simpleName ?: CLIENT_ERROR_CODE,
                    message = e.message ?: "HTTP client call failed",
                    requestId = requestId,
                    statusCode = response?.status?.value,
                    responseBody = response?.let { safeBodyAsText(it) },
                    headers = response?.headers?.entries()?.associate { it.key to it.value }.orEmpty(),
                )
            )
        }
    }

    private suspend fun safeBodyAsText(response: HttpResponse): String? = runCatching {
        response.bodyAsText()
    }.getOrNull()

    private companion object {
        const val REQUEST_ID_HEADER = "X-Request-Id"
        const val CLIENT_ERROR_CODE = "HTTP_CLIENT_ERROR"
    }
}
