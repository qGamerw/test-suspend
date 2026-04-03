package org.example.testsuspend.rest.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RestClientConfig {

    @Bean
    fun httpClient(): HttpClient = HttpClient(CIO)
}