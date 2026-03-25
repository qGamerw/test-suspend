package org.example.testsuspend.rest.client.decorator

import kotlinx.serialization.DeserializationStrategy
import org.example.testsuspend.rest.client.RawRestJsonClient
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.mapper.RestResponseMapper
import org.example.testsuspend.rest.model.RestCallResult
import org.example.testsuspend.rest.model.RestEndpoint
import org.example.testsuspend.rest.model.RestRequest

/**
 * Преобразует сырой HTTP-ответ в типизированный успешный или ошибочный DTO.
 */
class MappingRestJsonClientDecorator<S : Any, E : Any>(
    private val delegate: RawRestJsonClient,
    private val endpoint: RestEndpoint,
    private val responseMapper: RestResponseMapper,
    private val successDeserializer: DeserializationStrategy<S>,
    private val errorDeserializer: DeserializationStrategy<E>,
) : RestClient<S, E> {

    override suspend fun execute(request: RestRequest): RestCallResult<S, E> {
        val response = delegate.execute(request)

        return if (response.status in 200..299) {
            RestCallResult.Success(
                status = response.status,
                body = responseMapper.decodeSuccess(response.body, successDeserializer, endpoint),
                headers = response.headers,
            )
        } else {
            RestCallResult.Failure(
                status = response.status,
                error = responseMapper.decodeError(response.body, errorDeserializer, endpoint),
                rawBody = response.body.ifBlank { null },
                headers = response.headers,
            )
        }
    }
}
