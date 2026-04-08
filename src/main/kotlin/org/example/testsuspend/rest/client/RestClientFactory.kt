package org.example.testsuspend.rest.client

import io.ktor.client.HttpClient
import org.example.testsuspend.rest.dto.properties.HttpConfiguration

interface RestClientFactory {

    fun create(settings: HttpConfiguration): HttpClient
}
