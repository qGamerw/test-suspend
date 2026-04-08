package org.example.testsuspend.rest.dto.properties

interface HttpConfiguration {

    val baseUrl: String

    val createPaymentPath: String

    val resolveCustomerPath: String

    val timeoutMillis: Long

    val maxConnectionsCount: Int

    val maxConnectionsPerRoute: Int

    val keepAliveTimeMillis: Long

    val connectAttempts: Int

}