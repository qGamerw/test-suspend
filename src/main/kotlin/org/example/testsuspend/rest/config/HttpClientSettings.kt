package org.example.testsuspend.rest.config

data class HttpClientSettings(
    val timeoutMillis: Long,
    val maxConnectionsCount: Int,
    val maxConnectionsPerRoute: Int,
    val keepAliveTimeMillis: Long,
    val connectAttempts: Int,
)
