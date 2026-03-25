package org.example.testsuspend.rest.model

/**
 * Сырой HTTP-ответ, который возвращает транспортный слой до маппинга.
 */
data class RawRestResponse(
    val status: Int,
    val headers: Map<String, List<String>>,
    val body: String,
)
