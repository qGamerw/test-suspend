package org.example.testsuspend.rest.configuration

import io.ktor.util.reflect.typeInfo
import org.example.testsuspend.rest.HttpRestClientDelegate
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.client.impl.KtorRestClient
import org.example.testsuspend.rest.client.RestClientProvider
import org.example.testsuspend.rest.dto.properties.HttpConfiguration
import org.example.testsuspend.rest.impl.IntegrationLoggerHttpRestClientDelegate
import org.example.testsuspend.rest.model.CreatePaymentRequest
import org.example.testsuspend.rest.model.CreatePaymentResponse
import org.example.testsuspend.rest.model.ResolveCustomerRequest
import org.example.testsuspend.rest.model.ResolveCustomerResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RestConfiguration {

    @Bean
    fun createPaymentKtorRestClient(
        httpConfiguration: HttpConfiguration,
        restClientProvider: RestClientProvider,
    ): KtorRestClient<CreatePaymentRequest, CreatePaymentResponse> = KtorRestClient(
        { httpConfiguration.createPaymentPath },
        restClientProvider = restClientProvider,
        requestTypeInfo = typeInfo<CreatePaymentRequest>(),
        responseTypeInfo = typeInfo<CreatePaymentResponse>(),
    )

    @Bean
    fun resolveCustomerKtorRestClient(
        httpConfiguration: HttpConfiguration,
        restClientProvider: RestClientProvider,
    ): KtorRestClient<ResolveCustomerRequest, ResolveCustomerResponse> = KtorRestClient(
        { httpConfiguration.resolveCustomerPath },
        restClientProvider = restClientProvider,
        requestTypeInfo = typeInfo<ResolveCustomerRequest>(),
        responseTypeInfo = typeInfo<ResolveCustomerResponse>(),
    )

    @Bean
    fun createPaymentHttpRestClientDelegate(
        createPaymentKtorRestClient: KtorRestClient<CreatePaymentRequest, CreatePaymentResponse>,
    ): HttpRestClientDelegate<CreatePaymentRequest, CreatePaymentResponse> = IntegrationLoggerHttpRestClientDelegate(
        delegate = asHttpRestClientDelegate(createPaymentKtorRestClient),
    )

    @Bean
    fun resolveCustomerHttpRestClientDelegate(
        resolveCustomerKtorRestClient: KtorRestClient<ResolveCustomerRequest, ResolveCustomerResponse>,
    ): HttpRestClientDelegate<ResolveCustomerRequest, ResolveCustomerResponse> = IntegrationLoggerHttpRestClientDelegate(
        delegate = asHttpRestClientDelegate(resolveCustomerKtorRestClient),
    )

    private fun <Request, Response> asHttpRestClientDelegate(
        restClient: RestClient<Request, Response>,
    ): HttpRestClientDelegate<Request, Response> = object : HttpRestClientDelegate<Request, Response>,
        RestClient<Request, Response> by restClient {}
}
