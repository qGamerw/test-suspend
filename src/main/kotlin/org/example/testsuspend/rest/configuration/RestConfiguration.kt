package org.example.testsuspend.rest.configuration

import io.ktor.util.reflect.typeInfo
import org.example.testsuspend.rest.HttpRestClientDelegate
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.client.RestClientFactory
import org.example.testsuspend.rest.client.impl.KtorRestClient
import org.example.testsuspend.rest.client.impl.RestClientFactoryImpl
import org.example.testsuspend.rest.configuration.impl.HttpConfigurationImpl
import org.example.testsuspend.rest.impl.LoggerHttpRestClientDelegate
import org.example.testsuspend.rest.model.CreatePaymentRequest
import org.example.testsuspend.rest.model.CreatePaymentResponse
import org.example.testsuspend.rest.model.ResolveCustomerRequest
import org.example.testsuspend.rest.model.ResolveCustomerResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class RestConfiguration {

    @Bean
    fun restClientFactory(): RestClientFactory = RestClientFactoryImpl()

    @Bean("createPaymentHttpConfiguration")
    fun createPaymentHttpConfiguration(
        @Value("\${rest-client.create-payment-url}") url: String,
        @Value("\${rest-client.thread-count}") threadCount: Int,
        @Value("\${rest-client.timeout-millis}") timeoutMillis: Long,
        @Value("\${rest-client.max-connections-count}") maxConnectionsCount: Int,
        @Value("\${rest-client.max-connections-per-route}") maxConnectionsPerRoute: Int,
        @Value("\${rest-client.keep-alive-time-millis}") keepAliveTimeMillis: Long,
        @Value("\${rest-client.connect-attempts}") connectAttempts: Int,
    ): HttpConfiguration = buildHttpConfiguration(
        url = url,
        threadCount = threadCount,
        timeoutMillis = timeoutMillis,
        maxConnectionsCount = maxConnectionsCount,
        maxConnectionsPerRoute = maxConnectionsPerRoute,
        keepAliveTimeMillis = keepAliveTimeMillis,
        connectAttempts = connectAttempts,
    )

    @Bean("resolveCustomerHttpConfiguration")
    fun resolveCustomerHttpConfiguration(
        @Value("\${rest-client.resolve-customer-url}") url: String,
        @Value("\${rest-client.thread-count}") threadCount: Int,
        @Value("\${rest-client.timeout-millis}") timeoutMillis: Long,
        @Value("\${rest-client.max-connections-count}") maxConnectionsCount: Int,
        @Value("\${rest-client.max-connections-per-route}") maxConnectionsPerRoute: Int,
        @Value("\${rest-client.keep-alive-time-millis}") keepAliveTimeMillis: Long,
        @Value("\${rest-client.connect-attempts}") connectAttempts: Int,
    ): HttpConfiguration = buildHttpConfiguration(
        url = url,
        threadCount = threadCount,
        timeoutMillis = timeoutMillis,
        maxConnectionsCount = maxConnectionsCount,
        maxConnectionsPerRoute = maxConnectionsPerRoute,
        keepAliveTimeMillis = keepAliveTimeMillis,
        connectAttempts = connectAttempts,
    )

    @Bean(destroyMethod = "close")
    fun createPaymentKtorRestClient(
        @Qualifier("createPaymentHttpConfiguration") httpConfiguration: HttpConfiguration,
        restClientFactory: RestClientFactory,
    ): KtorRestClient<CreatePaymentRequest, CreatePaymentResponse> = KtorRestClient(
        httpConfiguration = httpConfiguration,
        restClientFactory = restClientFactory,
        requestTypeInfo = typeInfo<CreatePaymentRequest>(),
        responseTypeInfo = typeInfo<CreatePaymentResponse>(),
    )

    @Bean
    fun createPaymentHttpRestClientDelegate(
        createPaymentKtorRestClient: KtorRestClient<CreatePaymentRequest, CreatePaymentResponse>,
    ): HttpRestClientDelegate<CreatePaymentRequest, CreatePaymentResponse> = LoggerHttpRestClientDelegate(
        delegate = asHttpRestClientDelegate(createPaymentKtorRestClient),
    )

    @Bean(destroyMethod = "close")
    fun resolveCustomerKtorRestClient(
        @Qualifier("resolveCustomerHttpConfiguration") httpConfiguration: HttpConfiguration,
        restClientFactory: RestClientFactory,
    ): KtorRestClient<ResolveCustomerRequest, ResolveCustomerResponse> = KtorRestClient(
        httpConfiguration = httpConfiguration,
        restClientFactory = restClientFactory,
        requestTypeInfo = typeInfo<ResolveCustomerRequest>(),
        responseTypeInfo = typeInfo<ResolveCustomerResponse>(),
    )

    @Bean
    fun resolveCustomerHttpRestClientDelegate(
        resolveCustomerKtorRestClient: KtorRestClient<ResolveCustomerRequest, ResolveCustomerResponse>,
    ): HttpRestClientDelegate<ResolveCustomerRequest, ResolveCustomerResponse> = LoggerHttpRestClientDelegate(
        delegate = asHttpRestClientDelegate(resolveCustomerKtorRestClient),
    )

    private fun buildHttpConfiguration(
        url: String,
        threadCount: Int,
        timeoutMillis: Long,
        maxConnectionsCount: Int,
        maxConnectionsPerRoute: Int,
        keepAliveTimeMillis: Long,
        connectAttempts: Int,
    ): HttpConfiguration = HttpConfigurationImpl(
        url = url,
        threadCount = threadCount,
        timeoutMillis = timeoutMillis,
        maxConnectionsCount = maxConnectionsCount,
        maxConnectionsPerRoute = maxConnectionsPerRoute,
        keepAliveTimeMillis = keepAliveTimeMillis,
        connectAttempts = connectAttempts,
    )

    private fun <Request, Response> asHttpRestClientDelegate(
        restClient: RestClient<Request, Response>,
    ): HttpRestClientDelegate<Request, Response> = object : HttpRestClientDelegate<Request, Response>,
        RestClient<Request, Response> by restClient {}
}
