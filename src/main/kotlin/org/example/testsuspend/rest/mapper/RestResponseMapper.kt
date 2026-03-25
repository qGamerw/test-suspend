package org.example.testsuspend.rest.mapper

import kotlinx.serialization.DeserializationStrategy
import org.example.testsuspend.rest.model.RestEndpoint

/**
 * Преобразует сырые JSON-пейлоады в DTO приложения.
 */
interface RestResponseMapper {
    fun <T : Any> decodeSuccess(
        body: String,
        deserializer: DeserializationStrategy<T>,
        endpoint: RestEndpoint,
    ): T

    fun <T : Any> decodeError(
        body: String,
        deserializer: DeserializationStrategy<T>,
        endpoint: RestEndpoint,
    ): T?
}
