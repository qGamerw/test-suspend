package org.example.testsuspend.rest.configuration.impl

import org.example.testsuspend.rest.configuration.HttpConfiguration

class HttpConfigurationImpl(
    override val url: String,
    override val threadCount: Int,
    override val timeoutMillis: Long,
    override val maxConnectionsCount: Int,
    override val maxConnectionsPerRoute: Int,
    override val keepAliveTimeMillis: Long,
    override val connectAttempts: Int,
) : HttpConfiguration
