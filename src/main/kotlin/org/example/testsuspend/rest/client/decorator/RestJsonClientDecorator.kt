package org.example.testsuspend.rest.client.decorator

import org.example.testsuspend.rest.client.RawRestJsonClient
import org.example.testsuspend.rest.model.RawRestResponse
import org.example.testsuspend.rest.model.RestRequest

/**
 * Базовый класс для транспортных декораторов в цепочке REST-клиента.
 */
abstract class RestJsonClientDecorator(
    protected val delegate: RawRestJsonClient,
) : RawRestJsonClient {

    override suspend fun execute(request: RestRequest): RawRestResponse = delegate.execute(request)
}
