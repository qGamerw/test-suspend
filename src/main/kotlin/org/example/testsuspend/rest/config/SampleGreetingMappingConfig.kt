package org.example.testsuspend.rest.config

import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.client.decorator.MetricsRestJsonClientDecorator
import org.example.testsuspend.rest.mapper.RestResponseMapper
import org.example.testsuspend.rest.model.RestEndpoint
import org.example.testsuspend.sample.service.ApiErrorResponse
import org.example.testsuspend.sample.service.GreetingResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Конфигурация слоя маппинга для CRUD-операций greeting-сущности.
 */
@Configuration
class SampleGreetingMappingConfig : RestClientConfigSupport() {

    @Bean(name = [SampleGreetingRestClientConstants.CREATE_CLIENT])
    fun sampleGreetingCreateRestClient(
        @Qualifier(SampleGreetingRestClientConstants.CREATE_TRANSPORT) transportClient: MetricsRestJsonClientDecorator,
        @Qualifier(SampleGreetingRestClientConstants.CREATE_ENDPOINT) endpoint: RestEndpoint,
        responseMapper: RestResponseMapper,
    ): RestClient<GreetingResponse, ApiErrorResponse> = greetingMappedClient(transportClient, endpoint, responseMapper)

    @Bean(name = [SampleGreetingRestClientConstants.READ_CLIENT])
    fun sampleGreetingReadRestClient(
        @Qualifier(SampleGreetingRestClientConstants.READ_TRANSPORT) transportClient: MetricsRestJsonClientDecorator,
        @Qualifier(SampleGreetingRestClientConstants.READ_ENDPOINT) endpoint: RestEndpoint,
        responseMapper: RestResponseMapper,
    ): RestClient<GreetingResponse, ApiErrorResponse> = greetingMappedClient(transportClient, endpoint, responseMapper)

    @Bean(name = [SampleGreetingRestClientConstants.UPDATE_CLIENT])
    fun sampleGreetingUpdateRestClient(
        @Qualifier(SampleGreetingRestClientConstants.UPDATE_TRANSPORT) transportClient: MetricsRestJsonClientDecorator,
        @Qualifier(SampleGreetingRestClientConstants.UPDATE_ENDPOINT) endpoint: RestEndpoint,
        responseMapper: RestResponseMapper,
    ): RestClient<GreetingResponse, ApiErrorResponse> = greetingMappedClient(transportClient, endpoint, responseMapper)

    @Bean(name = [SampleGreetingRestClientConstants.DELETE_CLIENT])
    fun sampleGreetingDeleteRestClient(
        @Qualifier(SampleGreetingRestClientConstants.DELETE_TRANSPORT) transportClient: MetricsRestJsonClientDecorator,
        @Qualifier(SampleGreetingRestClientConstants.DELETE_ENDPOINT) endpoint: RestEndpoint,
        responseMapper: RestResponseMapper,
    ): RestClient<GreetingResponse, ApiErrorResponse> = greetingMappedClient(transportClient, endpoint, responseMapper)

    private fun greetingMappedClient(
        transportClient: MetricsRestJsonClientDecorator,
        endpoint: RestEndpoint,
        responseMapper: RestResponseMapper,
    ): RestClient<GreetingResponse, ApiErrorResponse> = mappedClient(
        transportClient = transportClient,
        endpoint = endpoint,
        responseMapper = responseMapper,
        successDeserializer = GreetingResponse.serializer(),
        errorDeserializer = ApiErrorResponse.serializer(),
    )
}
