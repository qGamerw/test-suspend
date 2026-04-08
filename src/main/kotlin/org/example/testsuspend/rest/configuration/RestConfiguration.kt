package org.example.testsuspend.rest.configuration

import io.ktor.util.reflect.typeInfo
import org.example.testsuspend.rest.HttpRestClientDelegate
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.client.RestClientFactory
import org.example.testsuspend.rest.client.impl.KtorRestClient
import org.example.testsuspend.rest.dto.properties.HttpConfiguration
import org.example.testsuspend.rest.impl.LoggerHttpRestClientDelegate
import org.example.testsuspend.rest.model.CreatePaymentRequest
import org.example.testsuspend.rest.model.CreatePaymentResponse
import org.example.testsuspend.rest.model.ResolveCustomerRequest
import org.example.testsuspend.rest.model.ResolveCustomerResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RestConfiguration {

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

    private fun <Request, Response> asHttpRestClientDelegate(
        restClient: RestClient<Request, Response>,
    ): HttpRestClientDelegate<Request, Response> = object : HttpRestClientDelegate<Request, Response>,
        RestClient<Request, Response> by restClient {}
}
