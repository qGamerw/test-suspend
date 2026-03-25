package org.example.testsuspend.sample.service

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.model.RestCallResult
import org.example.testsuspend.rest.model.RestRequest

/**
 * Базовый сервис для выполнения CRUD-операции через подготовленный RestClient.
 */
abstract class SampleGreetingOperationService(
    private val restClient: RestClient<GreetingResponse, ApiErrorResponse>,
    private val json: Json,
    private val action: String,
) {

    fun execute(name: String): RestCallResult<GreetingResponse, ApiErrorResponse> = runBlocking {
        val request = RestRequest(body = json.encodeToString(GreetingRequest(name)))

        when (val result = restClient.execute(request)) {
            is RestCallResult.Success -> result.copy(
                body = result.body.copy(message = "[$action] ${result.body.message}"),
            )
            is RestCallResult.Failure -> result
        }
    }
}
