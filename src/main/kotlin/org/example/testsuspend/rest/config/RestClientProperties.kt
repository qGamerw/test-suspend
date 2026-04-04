package org.example.testsuspend.rest.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rest-client")
data class RestClientProperties(
    val timeoutMillis: Long = 10_000,
    val maxConnectionsCount: Int = 64,
    val maxConnectionsPerRoute: Int = 16,
    val keepAliveTimeMillis: Long = 5_000,
    val connectAttempts: Int = 1,
    val requestIdHeaderName: String = "X-Request-Id",
    val createPaymentUrl: String = "https://example.org/payments",
    val resolveCustomerUrl: String = "https://example.org/customers/resolve",
)
