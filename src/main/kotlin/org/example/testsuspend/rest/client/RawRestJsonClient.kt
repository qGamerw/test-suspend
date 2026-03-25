package org.example.testsuspend.rest.client

import org.example.testsuspend.rest.model.RawRestResponse
import org.example.testsuspend.rest.model.RestRequest

/**
 * Низкоуровневый контракт для выполнения HTTP-вызова с возвратом немаппленного ответа.
 */
interface RawRestJsonClient {
    suspend fun execute(request: RestRequest): RawRestResponse
}
