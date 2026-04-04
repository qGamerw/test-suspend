package org.example.testsuspend.rest.config

import io.ktor.client.HttpClient

// Фабрика собирает новый HttpClient из набора runtime-настроек.
interface HttpClientFactory {
    fun create(settings: HttpClientSettings): HttpClient
}
