package org.example.testsuspend.rest.config

import io.ktor.client.HttpClient
import org.example.testsuspend.rest.client.decorator.MetricsRestJsonClientDecorator
import org.example.testsuspend.rest.logging.RestClientLogWriter
import org.example.testsuspend.rest.model.RestEndpoint
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Конфигурация транспортного слоя для CRUD-операций greeting-сущности.
 */
@Configuration
class SampleGreetingTransportConfig : RestClientConfigSupport() {

    @Bean(name = [SampleGreetingRestClientConstants.CREATE_TRANSPORT])
    fun sampleGreetingCreateTransportClient(
        httpClient: HttpClient,
        logWriter: RestClientLogWriter,
        @Qualifier(SampleGreetingRestClientConstants.CREATE_ENDPOINT) endpoint: RestEndpoint,
    ): MetricsRestJsonClientDecorator = transportClient(httpClient, logWriter, endpoint)

    @Bean(name = [SampleGreetingRestClientConstants.READ_TRANSPORT])
    fun sampleGreetingReadTransportClient(
        httpClient: HttpClient,
        logWriter: RestClientLogWriter,
        @Qualifier(SampleGreetingRestClientConstants.READ_ENDPOINT) endpoint: RestEndpoint,
    ): MetricsRestJsonClientDecorator = transportClient(httpClient, logWriter, endpoint)

    @Bean(name = [SampleGreetingRestClientConstants.UPDATE_TRANSPORT])
    fun sampleGreetingUpdateTransportClient(
        httpClient: HttpClient,
        logWriter: RestClientLogWriter,
        @Qualifier(SampleGreetingRestClientConstants.UPDATE_ENDPOINT) endpoint: RestEndpoint,
    ): MetricsRestJsonClientDecorator = transportClient(httpClient, logWriter, endpoint)

    @Bean(name = [SampleGreetingRestClientConstants.DELETE_TRANSPORT])
    fun sampleGreetingDeleteTransportClient(
        httpClient: HttpClient,
        logWriter: RestClientLogWriter,
        @Qualifier(SampleGreetingRestClientConstants.DELETE_ENDPOINT) endpoint: RestEndpoint,
    ): MetricsRestJsonClientDecorator = transportClient(httpClient, logWriter, endpoint)

}
