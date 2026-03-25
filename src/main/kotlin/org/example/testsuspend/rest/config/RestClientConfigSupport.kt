package org.example.testsuspend.rest.config

import io.ktor.client.HttpClient
import kotlinx.serialization.DeserializationStrategy
import org.example.testsuspend.rest.client.BaseRestJsonClient
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.client.decorator.LoggingRestJsonClientDecorator
import org.example.testsuspend.rest.client.decorator.MappingRestJsonClientDecorator
import org.example.testsuspend.rest.client.decorator.MetricsRestJsonClientDecorator
import org.example.testsuspend.rest.logging.RestClientLogWriter
import org.example.testsuspend.rest.mapper.RestResponseMapper
import org.example.testsuspend.rest.model.RestEndpoint

/**
 * Переиспользуемые factory-методы для сборки слоев REST-клиента.
 */
abstract class RestClientConfigSupport {

    protected fun transportClient(
        httpClient: HttpClient,
        logWriter: RestClientLogWriter,
        endpoint: RestEndpoint,
    ): MetricsRestJsonClientDecorator {
        val baseClient = BaseRestJsonClient(httpClient, endpoint)
        val loggingClient = LoggingRestJsonClientDecorator(baseClient, logWriter, endpoint)
        return MetricsRestJsonClientDecorator(loggingClient, logWriter, endpoint)
    }

    protected fun <S : Any, E : Any> mappedClient(
        transportClient: MetricsRestJsonClientDecorator,
        endpoint: RestEndpoint,
        responseMapper: RestResponseMapper,
        successDeserializer: DeserializationStrategy<S>,
        errorDeserializer: DeserializationStrategy<E>,
    ): RestClient<S, E> = MappingRestJsonClientDecorator(
        delegate = transportClient,
        endpoint = endpoint,
        responseMapper = responseMapper,
        successDeserializer = successDeserializer,
        errorDeserializer = errorDeserializer,
    )
}
