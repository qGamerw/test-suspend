package org.example.testsuspend.sample.service.impl

import kotlinx.serialization.json.Json
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.config.SampleGreetingRestClientConstants
import org.example.testsuspend.sample.service.ApiErrorResponse
import org.example.testsuspend.sample.service.GreetingResponse
import org.example.testsuspend.sample.service.SampleGreetingOperationService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/** Сервис создания greeting-сущности. */
@Service
class SampleGreetingCreateService(
    @Qualifier(SampleGreetingRestClientConstants.CREATE_CLIENT) restClient: RestClient<GreetingResponse, ApiErrorResponse>,
    json: Json,
) : SampleGreetingOperationService(restClient, json, SampleGreetingRestClientConstants.CREATE_ACTION) {

    fun createGreeting(name: String) = execute(name)
}
