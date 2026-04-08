package org.example.testsuspend.rest.client.impl

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.jackson.jackson
import org.example.testsuspend.rest.client.RestClientFactory
import org.example.testsuspend.rest.dto.properties.HttpConfiguration
import org.springframework.stereotype.Component

@Component
class RestClientFactoryImpl : RestClientFactory {

    override fun create(settings: HttpConfiguration): HttpClient = HttpClient(CIO) {
        defaultRequest {
            header(HttpHeaders.Accept, ContentType.Application.Json)
            url(settings.baseUrl)
        }

        engine {
            threadsCount = settings.threadCount
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
