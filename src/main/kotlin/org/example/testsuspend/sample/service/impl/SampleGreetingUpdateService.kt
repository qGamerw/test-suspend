package org.example.testsuspend.sample.service.impl

import kotlinx.serialization.json.Json
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.config.SampleGreetingRestClientConstants
import org.example.testsuspend.sample.service.ApiErrorResponse
import org.example.testsuspend.sample.service.GreetingResponse
import org.example.testsuspend.sample.service.SampleGreetingOperationService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/** Сервис обновления greeting-сущности. */
@Service
class SampleGreetingUpdateService(
    @Qualifier(SampleGreetingRestClientConstants.UPDATE_CLIENT) restClient: RestClient<GreetingResponse, ApiErrorResponse>,
    json: Json,
) : SampleGreetingOperationService(restClient, json, SampleGreetingRestClientConstants.UPDATE_ACTION) {

    fun updateGreeting(name: String) = execute(name)
}
