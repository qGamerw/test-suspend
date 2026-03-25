package org.example.testsuspend.rest.mapper

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.example.testsuspend.rest.exception.ResponseMappingException
import org.example.testsuspend.rest.model.RestEndpoint

/**
 * Реализация маппера ответов на базе `kotlinx.serialization`.
 */
class KotlinxRestResponseMapper(
    private val json: Json,
) : RestResponseMapper {

    override fun <T : Any> decodeSuccess(
        body: String,
        deserializer: DeserializationStrategy<T>,
        endpoint: RestEndpoint,
    ): T = decode(body, deserializer, endpoint, "success")

    override fun <T : Any> decodeError(
        body: String,
        deserializer: DeserializationStrategy<T>,
        endpoint: RestEndpoint,
    ): T? {
        if (body.isBlank()) {
            return null
        }

        return decode(body, deserializer, endpoint, "error")
    }

    private fun <T : Any> decode(
        body: String,
        deserializer: DeserializationStrategy<T>,
        endpoint: RestEndpoint,
        responseType: String,
    ): T {
        return try {
            json.decodeFromString(deserializer, body)
        } catch (exception: SerializationException) {
            throw ResponseMappingException(
                message = "Failed to map $responseType response for ${endpoint.operationName}",
                cause = exception,
            )
        }
    }
}
