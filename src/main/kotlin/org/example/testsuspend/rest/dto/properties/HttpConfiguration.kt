package org.example.testsuspend.rest.dto.properties

interface HttpConfiguration {

    val baseUrl: String

    val path: String

    val threadCount: Int

    val timeoutMillis: Long

    val maxConnectionsCount: Int

    val maxConnectionsPerRoute: Int

    val keepAliveTimeMillis: Long

    val connectAttempts: Int

}