package org.example.testsuspend.rest.config

import org.example.testsuspend.rest.client.KtorRestClient
import org.example.testsuspend.rest.client.LoggingRestClient
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.logging.RestClientLogWriter
import org.example.testsuspend.rest.logging.Slf4jRestClientLogWriter
import org.example.testsuspend.rest.model.CreatePaymentRequest
import org.example.testsuspend.rest.model.CreatePaymentResponse
import org.example.testsuspend.rest.model.ResolveCustomerRequest
import org.example.testsuspend.rest.model.ResolveCustomerResponse
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(RestClientProperties::class)
class RestClientConfig {

    @Bean
    // Factory создаёт новый HttpClient при старте и при runtime-реконфигурации.
    fun httpClientFactory(): HttpClientFactory = KtorHttpClientFactory()

    @Bean
    // Provider хранит текущий HttpClient и даёт единый вход для его подмены.
    fun httpClientProvider(
        properties: RestClientProperties,
        httpClientFactory: HttpClientFactory,
    ): HttpClientProvider = HttpClientProvider(
        httpClientFactory = httpClientFactory,
        initialSettings = HttpClientSettings(
            timeoutMillis = properties.timeoutMillis,
            maxConnectionsCount = properties.maxConnectionsCount,
            maxConnectionsPerRoute = properties.maxConnectionsPerRoute,
            keepAliveTimeMillis = properties.keepAliveTimeMillis,
            connectAttempts = properties.connectAttempts,
        ),
    )

    @Bean
    // Логирование вынесено в отдельный слой, чтобы не смешивать transport и observability.
    fun restClientLogWriter(): RestClientLogWriter = Slf4jRestClientLogWriter()

    @Bean("createPaymentRestClient")
    // Typed-клиент собирается как wrapper: logging -> ktor transport.
    fun createPaymentRestClient(
        httpClientProvider: HttpClientProvider,
        restClientLogWriter: RestClientLogWriter,
        properties: RestClientProperties,
    ): RestClient<CreatePaymentRequest, CreatePaymentResponse> = LoggingRestClient(
        operationName = "create-payment",
        delegate = KtorRestClient(
            httpClientProvider = httpClientProvider,
            url = properties.createPaymentUrl,
            requestType = CreatePaymentRequest::class,
            responseType = CreatePaymentResponse::class,
            requestIdHeaderName = properties.requestIdHeaderName,
        ),
        logWriter = restClientLogWriter,
    )

    @Bean("resolveCustomerRestClient")
    // Отдельный bean для другого контракта, но на том же reloadable HttpClientProvider.
    fun resolveCustomerRestClient(
        httpClientProvider: HttpClientProvider,
        restClientLogWriter: RestClientLogWriter,
        properties: RestClientProperties,
    ): RestClient<ResolveCustomerRequest, ResolveCustomerResponse> = LoggingRestClient(
        operationName = "resolve-customer",
        delegate = KtorRestClient(
            httpClientProvider = httpClientProvider,
            url = properties.resolveCustomerUrl,
            requestType = ResolveCustomerRequest::class,
            responseType = ResolveCustomerResponse::class,
            requestIdHeaderName = properties.requestIdHeaderName,
        ),
        logWriter = restClientLogWriter,
    )
}
