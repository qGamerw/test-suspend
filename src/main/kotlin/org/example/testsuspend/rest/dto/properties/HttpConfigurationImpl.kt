package org.example.testsuspend.rest.dto.properties

class HttpConfigurationImpl(
    override val baseUrl: String,
    override val resolveCustomerPath: String,
    override val createPaymentPath: String,
    override val timeoutMillis: Long,
    override val maxConnectionsCount: Int,
    override val maxConnectionsPerRoute: Int,
    override val keepAliveTimeMillis: Long,
    override val connectAttempts: Int,
) : HttpConfiguration
