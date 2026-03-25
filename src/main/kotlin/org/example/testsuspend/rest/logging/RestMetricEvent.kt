package org.example.testsuspend.rest.logging

/**
 * Структурированное событие метрик, которое формирует декоратор метрик.
 */
data class RestMetricEvent(
    val operationName: String,
    val method: String,
    val status: Int?,
    val outcome: String,
    val durationMillis: Long,
    val count: Long = 1,
)
