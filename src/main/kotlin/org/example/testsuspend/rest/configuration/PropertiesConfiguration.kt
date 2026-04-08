package org.example.testsuspend.rest.configuration

import org.example.testsuspend.rest.dto.properties.HttpConfiguration
import org.example.testsuspend.rest.dto.properties.HttpConfigurationImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PropertiesConfiguration {

    @Bean("createPaymentHttpConfiguration")
    fun createPaymentHttpConfiguration(
        @Value("\${rest-client.base-url}") url: String,
        @Value("\${rest-client.create-payment-path}") path: String,
        @Value("\${rest-client.thread-count}") threadCount: Int,
        @Value("\${rest-client.timeout-millis}") timeoutMillis: Long,
        @Value("\${rest-client.max-connections-count}") maxConnectionsCount: Int,
        @Value("\${rest-client.max-connections-per-route}") maxConnectionsPerRoute: Int,
        @Value("\${rest-client.keep-alive-time-millis}") keepAliveTimeMillis: Long,
        @Value("\${rest-client.connect-attempts}") connectAttempts: Int,
    ): HttpConfiguration = buildHttpConfiguration(
        url = url,
        path = path,
        threadCount = threadCount,
        timeoutMillis = timeoutMillis,
        maxConnectionsCount = maxConnectionsCount,
        maxConnectionsPerRoute = maxConnectionsPerRoute,
        keepAliveTimeMillis = keepAliveTimeMillis,
        connectAttempts = connectAttempts,
    )

    @Bean("resolveCustomerHttpConfiguration")
    fun resolveCustomerHttpConfiguration(
        @Value("\${rest-client.base-url}") url: String,
        @Value("\${rest-client.resolve-customer-path}") path: String,
        @Value("\${rest-client.thread-count}") threadCount: Int,
        @Value("\${rest-client.timeout-millis}") timeoutMillis: Long,
        @Value("\${rest-client.max-connections-count}") maxConnectionsCount: Int,
        @Value("\${rest-client.max-connections-per-route}") maxConnectionsPerRoute: Int,
        @Value("\${rest-client.keep-alive-time-millis}") keepAliveTimeMillis: Long,
        @Value("\${rest-client.connect-attempts}") connectAttempts: Int,
    ): HttpConfiguration = buildHttpConfiguration(
        url = url,
        path = path,
        threadCount = threadCount,
        timeoutMillis = timeoutMillis,
        maxConnectionsCount = maxConnectionsCount,
        maxConnectionsPerRoute = maxConnectionsPerRoute,
        keepAliveTimeMillis = keepAliveTimeMillis,
        connectAttempts = connectAttempts,
    )

    private fun buildHttpConfiguration(
        url: String,
        path: String,
        threadCount: Int,
        timeoutMillis: Long,
        maxConnectionsCount: Int,
        maxConnectionsPerRoute: Int,
        keepAliveTimeMillis: Long,
        connectAttempts: Int,
    ): HttpConfiguration = HttpConfigurationImpl(
        baseUrl = url,
        path = path,
        threadCount = threadCount,
        timeoutMillis = timeoutMillis,
        maxConnectionsCount = maxConnectionsCount,
        maxConnectionsPerRoute = maxConnectionsPerRoute,
        keepAliveTimeMillis = keepAliveTimeMillis,
        connectAttempts = connectAttempts,
    )

}