package org.example.testsuspend.rest.configuration

interface HttpConfiguration {

    val url: String

    val threadCount: Int

    val timeoutMillis: Long

    val maxConnectionsCount: Int

    val maxConnectionsPerRoute: Int

    val keepAliveTimeMillis: Long

    val connectAttempts: Int

}
