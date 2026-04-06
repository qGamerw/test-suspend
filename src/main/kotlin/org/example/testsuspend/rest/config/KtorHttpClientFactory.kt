package org.example.testsuspend.rest.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson

open class KtorHttpClientFactory {

    open fun create(settings: HttpClientSettings): HttpClient = HttpClient(CIO) {
        engine {
            // Ограничиваем количество engine threads, чтобы клиент не забирал CPU у остальных процессов.
            threadsCount = settings.threadCount

            // Ограничиваем общий пул соединений, чтобы клиент не съедал все ресурсы процесса.
            maxConnectionsCount = settings.maxConnectionsCount

            endpoint.apply {
                // Эти лимиты можно безопасно менять только через пересоздание HttpClient.
                maxConnectionsPerRoute = settings.maxConnectionsPerRoute
                keepAliveTime = settings.keepAliveTimeMillis
                connectAttempts = settings.connectAttempts
                connectTimeout = settings.timeoutMillis
                socketTimeout = settings.timeoutMillis
            }
        }

        // Таймауты задаются на уровне клиента, поэтому тоже входят в runtime-реконфигурацию.
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
