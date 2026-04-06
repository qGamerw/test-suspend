package org.example.testsuspend.rest.config

import io.ktor.http.HttpMethod
import org.example.testsuspend.rest.client.IntegrationInfo
import org.example.testsuspend.rest.client.KtorRestClient
import org.example.testsuspend.rest.client.LoggingRestClient
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.client.TypeInfoSerializable
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
    fun httpClientFactory(): KtorHttpClientFactory = KtorHttpClientFactory()

    @Bean("createPaymentHttpConfiguration")
    fun createPaymentHttpConfiguration(properties: RestClientProperties): HttpConfiguration = HttpConfiguration(
        url = properties.createPaymentUrl,
        requestIdHeaderName = properties.requestIdHeaderName,
        method = HttpMethod.Post,
        initialSettings = HttpClientSettings(
            threadCount = properties.threadCount,
            timeoutMillis = properties.timeoutMillis,
            maxConnectionsCount = properties.maxConnectionsCount,
            maxConnectionsPerRoute = properties.maxConnectionsPerRoute,
            keepAliveTimeMillis = properties.keepAliveTimeMillis,
            connectAttempts = properties.connectAttempts,
        ),
    )

    @Bean("resolveCustomerHttpConfiguration")
    fun resolveCustomerHttpConfiguration(properties: RestClientProperties): HttpConfiguration = HttpConfiguration(
        url = properties.resolveCustomerUrl,
        requestIdHeaderName = properties.requestIdHeaderName,
        method = HttpMethod.Post,
        initialSettings = HttpClientSettings(
            threadCount = properties.threadCount,
            timeoutMillis = properties.timeoutMillis,
            maxConnectionsCount = properties.maxConnectionsCount,
            maxConnectionsPerRoute = properties.maxConnectionsPerRoute,
            keepAliveTimeMillis = properties.keepAliveTimeMillis,
            connectAttempts = properties.connectAttempts,
        ),
    )

    @Bean("createPaymentIntegrationInfo")
    fun createPaymentIntegrationInfo(): IntegrationInfo<CreatePaymentRequest> = IntegrationInfo(
        name = "create-payment",
        requestIdProvider = CreatePaymentRequest::requestId,
        customHeadersProvider = CreatePaymentRequest::customHeaders,
    )

    @Bean("resolveCustomerIntegrationInfo")
    fun resolveCustomerIntegrationInfo(): IntegrationInfo<ResolveCustomerRequest> = IntegrationInfo(
        name = "resolve-customer",
        requestIdProvider = ResolveCustomerRequest::requestId,
        customHeadersProvider = ResolveCustomerRequest::customHeaders,
    )

    @Bean
    // Логирование вынесено в отдельный слой, чтобы не смешивать transport и observability.
    fun restClientLogWriter(): RestClientLogWriter = Slf4jRestClientLogWriter()

    @Bean("createPaymentRestClient")
    // Typed-клиент собирается как wrapper: logging -> ktor transport.
    fun createPaymentRestClient(
        httpClientFactory: KtorHttpClientFactory,
        @org.springframework.beans.factory.annotation.Qualifier("createPaymentHttpConfiguration")
        httpConfiguration: HttpConfiguration,
        @org.springframework.beans.factory.annotation.Qualifier("createPaymentIntegrationInfo")
        integrationInfo: IntegrationInfo<CreatePaymentRequest>,
        restClientLogWriter: RestClientLogWriter,
    ): RestClient<CreatePaymentRequest, CreatePaymentResponse> = LoggingRestClient(
        operationName = integrationInfo.name,
        delegate = KtorRestClient(
            configuration = httpConfiguration,
            httpClientFactory = httpClientFactory,
            serializer = TypeInfoSerializable(CreatePaymentRequest::class, CreatePaymentResponse::class),
            integrationInfo = integrationInfo,
        ),
        logWriter = restClientLogWriter,
        requestIdProvider = integrationInfo.requestIdProvider,
    )

    @Bean("resolveCustomerRestClient")
    // Отдельный bean для другого контракта, но на том же reloadable HttpClientProvider.
    fun resolveCustomerRestClient(
        httpClientFactory: KtorHttpClientFactory,
        @org.springframework.beans.factory.annotation.Qualifier("resolveCustomerHttpConfiguration")
        httpConfiguration: HttpConfiguration,
        @org.springframework.beans.factory.annotation.Qualifier("resolveCustomerIntegrationInfo")
        integrationInfo: IntegrationInfo<ResolveCustomerRequest>,
        restClientLogWriter: RestClientLogWriter,
    ): RestClient<ResolveCustomerRequest, ResolveCustomerResponse> = LoggingRestClient(
        operationName = integrationInfo.name,
        delegate = KtorRestClient(
            configuration = httpConfiguration,
            httpClientFactory = httpClientFactory,
            serializer = TypeInfoSerializable(ResolveCustomerRequest::class, ResolveCustomerResponse::class),
            integrationInfo = integrationInfo,
        ),
        logWriter = restClientLogWriter,
        requestIdProvider = integrationInfo.requestIdProvider,
    )
}
