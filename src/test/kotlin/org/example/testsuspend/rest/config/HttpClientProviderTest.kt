package org.example.testsuspend.rest.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class HttpClientProviderTest {

    @Test
    fun `reconfigure replaces current http client and stores new settings`() {
        val factoryCalls = AtomicInteger()
        val provider = HttpClientProvider(
            httpClientFactory = object : HttpClientFactory {
                override fun create(settings: HttpClientSettings): HttpClient {
                    factoryCalls.incrementAndGet()
                    return HttpClient(MockEngine { respond("", HttpStatusCode.OK) })
                }
            },
            initialSettings = HttpClientSettings(
                timeoutMillis = 10_000,
                maxConnectionsCount = 64,
                maxConnectionsPerRoute = 16,
                keepAliveTimeMillis = 5_000,
                connectAttempts = 1,
            ),
        )

        val initialClient = provider.get()
        val newSettings = HttpClientSettings(
            timeoutMillis = 3_000,
            maxConnectionsCount = 32,
            maxConnectionsPerRoute = 8,
            keepAliveTimeMillis = 2_000,
            connectAttempts = 2,
        )

        provider.reconfigure(newSettings)

        assertEquals(2, factoryCalls.get())
        assertEquals(newSettings, provider.settings())
        assertNotSame(initialClient, provider.get())
    }
}
