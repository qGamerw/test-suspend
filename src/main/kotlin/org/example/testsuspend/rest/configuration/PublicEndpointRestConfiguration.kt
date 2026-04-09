package org.example.testsuspend.rest.configuration

import com.fasterxml.jackson.core.type.TypeReference
import org.example.testsuspend.rest.HttpRestClientDelegate
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.client.RestClientProvider
import org.example.testsuspend.rest.client.impl.KtorRestClient
import org.example.testsuspend.rest.dto.properties.HttpConfiguration
import org.example.testsuspend.rest.dto.properties.HttpConfigurationImpl
import org.example.testsuspend.rest.impl.IntegrationLoggerHttpRestClientDelegate
import org.example.testsuspend.rest.model.HttpBinResponse
import org.example.testsuspend.rest.model.PostmanEchoResponse
import org.example.testsuspend.rest.model.PublicPostRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PublicEndpointRestConfiguration {

    @Bean("postmanEchoHttpConfiguration")
    fun postmanEchoHttpConfiguration(
        @Value("\${rest-client.timeout-millis}") timeoutMillis: Long,
        @Value("\${rest-client.max-connections-count}") maxConnectionsCount: Int,
        @Value("\${rest-client.max-connections-per-route}") maxConnectionsPerRoute: Int,
        @Value("\${rest-client.keep-alive-time-millis}") keepAliveTimeMillis: Long,
        @Value("\${rest-client.connect-attempts}") connectAttempts: Int,
    ): HttpConfiguration = HttpConfigurationImpl(
        baseUrl = "https://postman-echo.com",
        resolveCustomerPath = "/post",
        createPaymentPath = "/post",
        timeoutMillis = timeoutMillis,
        maxConnectionsCount = maxConnectionsCount,
        maxConnectionsPerRoute = maxConnectionsPerRoute,
        keepAliveTimeMillis = keepAliveTimeMillis,
        connectAttempts = connectAttempts,
    )

    @Bean("httpBinHttpConfiguration")
    fun httpBinHttpConfiguration(
        @Value("\${rest-client.timeout-millis}") timeoutMillis: Long,
        @Value("\${rest-client.max-connections-count}") maxConnectionsCount: Int,
        @Value("\${rest-client.max-connections-per-route}") maxConnectionsPerRoute: Int,
        @Value("\${rest-client.keep-alive-time-millis}") keepAliveTimeMillis: Long,
        @Value("\${rest-client.connect-attempts}") connectAttempts: Int,
    ): HttpConfiguration = HttpConfigurationImpl(
        baseUrl = "https://httpbin.org",
        resolveCustomerPath = "/post",
        createPaymentPath = "/post",
        timeoutMillis = timeoutMillis,
        maxConnectionsCount = maxConnectionsCount,
        maxConnectionsPerRoute = maxConnectionsPerRoute,
        keepAliveTimeMillis = keepAliveTimeMillis,
        connectAttempts = connectAttempts,
    )

    @Bean("postmanEchoRestClientProvider")
    fun postmanEchoRestClientProvider(
        @org.springframework.beans.factory.annotation.Qualifier("postmanEchoHttpConfiguration")
        httpConfiguration: HttpConfiguration,
    ) = RestClientProvider(httpConfiguration)

    @Bean("httpBinRestClientProvider")
    fun httpBinRestClientProvider(
        @org.springframework.beans.factory.annotation.Qualifier("httpBinHttpConfiguration")
        httpConfiguration: HttpConfiguration,
    ) = RestClientProvider(httpConfiguration)

    @Bean("postmanEchoRestClient")
    fun postmanEchoRestClient(
        @org.springframework.beans.factory.annotation.Qualifier("postmanEchoRestClientProvider")
        restClientProvider: RestClientProvider,
    ): KtorRestClient<PublicPostRequest, PostmanEchoResponse> = KtorRestClient(
        getPath = { "/post" },
        restClientProvider = restClientProvider,
        typeReference = object : TypeReference<PostmanEchoResponse>() {},
    )

    @Bean("httpBinRestClient")
    fun httpBinRestClient(
        @org.springframework.beans.factory.annotation.Qualifier("httpBinRestClientProvider")
        restClientProvider: RestClientProvider,
    ): KtorRestClient<PublicPostRequest, HttpBinResponse> = KtorRestClient(
        getPath = { "/post" },
        restClientProvider = restClientProvider,
        typeReference = object : TypeReference<HttpBinResponse>() {},
    )

    @Bean("postmanEchoHttpRestClientDelegate")
    fun postmanEchoHttpRestClientDelegate(
        @org.springframework.beans.factory.annotation.Qualifier("postmanEchoRestClient")
        restClient: KtorRestClient<PublicPostRequest, PostmanEchoResponse>,
    ): HttpRestClientDelegate<PublicPostRequest, PostmanEchoResponse> = IntegrationLoggerHttpRestClientDelegate(
        delegate = asHttpRestClientDelegate(restClient),
    )

    @Bean("httpBinHttpRestClientDelegate")
    fun httpBinHttpRestClientDelegate(
        @org.springframework.beans.factory.annotation.Qualifier("httpBinRestClient")
        restClient: KtorRestClient<PublicPostRequest, HttpBinResponse>,
    ): HttpRestClientDelegate<PublicPostRequest, HttpBinResponse> = IntegrationLoggerHttpRestClientDelegate(
        delegate = asHttpRestClientDelegate(restClient),
    )

    private fun <Request, Response> asHttpRestClientDelegate(
        restClient: RestClient<Request, Response>,
    ): HttpRestClientDelegate<Request, Response> = object : HttpRestClientDelegate<Request, Response>,
        RestClient<Request, Response> by restClient {}
}
