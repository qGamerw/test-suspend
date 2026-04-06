package org.example.testsuspend.rest.config

import io.ktor.http.HttpMethod
import java.util.concurrent.atomic.AtomicReference

class HttpConfiguration(
    val url: String,
    val requestIdHeaderName: String,
    val method: HttpMethod = HttpMethod.Post,
    initialSettings: HttpClientSettings,
) {

    private val settingsRef = AtomicReference(initialSettings)

    fun clientSettings(): HttpClientSettings = settingsRef.get()

    // Конфигурация меняется отдельно, а применение к KtorRestClient происходит через restart().
    fun reconfigure(settings: HttpClientSettings) {
        settingsRef.set(settings)
    }
}
