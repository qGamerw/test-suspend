package org.example.testsuspend.rest.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.util.concurrent.atomic.AtomicReference
import org.example.testsuspend.rest.dto.properties.HttpConfiguration

class RestClientProvider(
    private val settings: HttpConfiguration
): Reconfigurable, AutoCloseable {

    private val clientRef = AtomicReference(create())

    fun getClient(): HttpClient = clientRef.get()

    override fun reconfigure() {
        val oldClient = clientRef.getAndSet(create())
        oldClient.close()
    }

    override fun close() {
        // todo release?
        clientRef.get().close()
    }

    private fun create(): HttpClient = HttpClient(CIO) {
        defaultRequest {
            contentType(ContentType.Application.Json)
            url(settings.baseUrl)
        }

        engine {
            maxConnectionsCount = settings.maxConnectionsCount

            endpoint.apply {
                maxConnectionsPerRoute = settings.maxConnectionsPerRoute
                keepAliveTime = settings.keepAliveTimeMillis
                connectAttempts = settings.connectAttempts
                connectTimeout = settings.timeoutMillis
                socketTimeout = settings.timeoutMillis
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = settings.timeoutMillis
            connectTimeoutMillis = settings.timeoutMillis
            socketTimeoutMillis = settings.timeoutMillis
        }
    }
}
