package org.example.testsuspend.rest.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.jackson.jackson
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.isActive
import org.example.testsuspend.rest.dto.properties.HttpConfiguration

class RestClientProvider(
    private val settings: HttpConfiguration
): Reconfigurable, AutoCloseable {

    val clientRef = AtomicReference(create())

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
            header(HttpHeaders.Accept, ContentType.Application.Json)
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

        install(ContentNegotiation) {
            jackson {
                findAndRegisterModules()
            }
        }
    }
}
