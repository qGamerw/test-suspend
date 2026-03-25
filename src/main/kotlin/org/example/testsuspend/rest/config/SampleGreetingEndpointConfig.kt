package org.example.testsuspend.rest.config

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import org.example.testsuspend.rest.model.RestEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Конфигурация endpoint'ов для CRUD-операций greeting-сущности.
 */
@Configuration
class SampleGreetingEndpointConfig {

    @Bean(name = [SampleGreetingRestClientConstants.CREATE_ENDPOINT])
    fun sampleGreetingCreateEndpoint(): RestEndpoint = greetingEndpoint(
        operationName = SampleGreetingRestClientConstants.CREATE_OPERATION,
        url = "https://example.org/api/greetings/create",
    )

    @Bean(name = [SampleGreetingRestClientConstants.READ_ENDPOINT])
    fun sampleGreetingReadEndpoint(): RestEndpoint = greetingEndpoint(
        operationName = SampleGreetingRestClientConstants.READ_OPERATION,
        url = "https://example.org/api/greetings/read",
    )

    @Bean(name = [SampleGreetingRestClientConstants.UPDATE_ENDPOINT])
    fun sampleGreetingUpdateEndpoint(): RestEndpoint = greetingEndpoint(
        operationName = SampleGreetingRestClientConstants.UPDATE_OPERATION,
        url = "https://example.org/api/greetings/update",
    )

    @Bean(name = [SampleGreetingRestClientConstants.DELETE_ENDPOINT])
    fun sampleGreetingDeleteEndpoint(): RestEndpoint = greetingEndpoint(
        operationName = SampleGreetingRestClientConstants.DELETE_OPERATION,
        url = "https://example.org/api/greetings/delete",
    )

    private fun greetingEndpoint(operationName: String, url: String): RestEndpoint = RestEndpoint(
        operationName = operationName,
        method = HttpMethod.Post,
        url = url,
        headers = mapOf("Accept" to "application/json"),
        contentType = ContentType.Application.Json,
        timeoutMillis = 3_000,
    )
}
