package org.example.testsuspend.rest.config

import io.ktor.client.HttpClient
import java.util.concurrent.atomic.AtomicReference

// Provider хранит текущий HttpClient и умеет атомарно подменять его после реконфигурации.
class HttpClientProvider(
    private val httpClientFactory: HttpClientFactory,
    initialSettings: HttpClientSettings,
) {

    private val settingsRef = AtomicReference(initialSettings)
    private val clientRef = AtomicReference(httpClientFactory.create(initialSettings))

    fun get(): HttpClient = clientRef.get()

    fun settings(): HttpClientSettings = settingsRef.get()

    fun reconfigure(settings: HttpClientSettings) {
        // Сначала создаём новый клиент, чтобы не оставлять систему без рабочего экземпляра.
        val newClient = httpClientFactory.create(settings)
        val oldClient = clientRef.getAndSet(newClient)
        settingsRef.set(settings)
        // Старый клиент закрываем после подмены ссылки.
        oldClient.close()
    }
}
