package org.example.testsuspend.rest.client

import org.example.testsuspend.rest.model.RestCallResult
import org.example.testsuspend.rest.model.RestRequest

/**
 * Высокоуровневый контракт для выполнения HTTP-вызова с маппингом успешного и ошибочного ответа.
 */
interface RestClient<S : Any, E : Any> {
    suspend fun execute(request: RestRequest): RestCallResult<S, E>
}
