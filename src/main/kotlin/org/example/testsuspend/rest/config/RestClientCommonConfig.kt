package org.example.testsuspend.rest.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.example.testsuspend.rest.logging.RestClientLogWriter
import org.example.testsuspend.rest.logging.Slf4jRestClientLogWriter
import org.example.testsuspend.rest.mapper.KotlinxRestResponseMapper
import org.example.testsuspend.rest.mapper.RestResponseMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Общая конфигурация инфраструктуры REST-клиента.
 */
@Configuration
@OptIn(ExperimentalSerializationApi::class)
class RestClientCommonConfig {

    @Bean
    fun restJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    @Bean
    fun restHttpClient(json: Json): HttpClient = HttpClient(CIO) {
        expectSuccess = false
        install(ContentNegotiation) {
            json(json)
        }
    }

    @Bean
    fun restClientLogWriter(): RestClientLogWriter = Slf4jRestClientLogWriter()

    @Bean
    fun restResponseMapper(json: Json): RestResponseMapper = KotlinxRestResponseMapper(json)
}
