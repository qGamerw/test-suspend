package org.example.testsuspend.rest.configuration

import org.example.testsuspend.rest.client.RestClientProvider
import org.example.testsuspend.rest.dto.properties.HttpConfiguration
import org.example.testsuspend.rest.dto.properties.HttpConfigurationImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PropertiesConfiguration {

    @Bean("defaultRestClientProvider")
    fun getRestClientProvider(
        @org.springframework.beans.factory.annotation.Qualifier("httpConfiguration")
        httpConfiguration: HttpConfiguration
    ) = RestClientProvider(httpConfiguration)

    @Bean("httpConfiguration")
    fun getHttpConfiguration(
        @Value("\${rest-client.base-url}") url: String,
        @Value("\${rest-client.create-payment-path}") createPaymentPath: String,
        @Value("\${rest-client.resolve-customer-path}") resolveCustomerPath: String,
        @Value("\${rest-client.timeout-millis}") timeoutMillis: Long,
        @Value("\${rest-client.max-connections-count}") maxConnectionsCount: Int,
        @Value("\${rest-client.max-connections-per-route}") maxConnectionsPerRoute: Int,
        @Value("\${rest-client.keep-alive-time-millis}") keepAliveTimeMillis: Long,
        @Value("\${rest-client.connect-attempts}") connectAttempts: Int,
    ): HttpConfiguration = HttpConfigurationImpl(
        baseUrl = url,
        resolveCustomerPath = resolveCustomerPath,
        createPaymentPath = createPaymentPath,
        timeoutMillis = timeoutMillis,
        maxConnectionsCount = maxConnectionsCount,
        maxConnectionsPerRoute = maxConnectionsPerRoute,
        keepAliveTimeMillis = keepAliveTimeMillis,
        connectAttempts = connectAttempts,
    )

}
