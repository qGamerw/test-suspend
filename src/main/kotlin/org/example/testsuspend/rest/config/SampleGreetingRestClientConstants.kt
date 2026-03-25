package org.example.testsuspend.rest.config

/**
 * Константы имен bean'ов и operation name для sample greeting интеграции.
 */
object SampleGreetingRestClientConstants {
    const val CREATE_OPERATION = "sample-greeting-create"
    const val READ_OPERATION = "sample-greeting-read"
    const val UPDATE_OPERATION = "sample-greeting-update"
    const val DELETE_OPERATION = "sample-greeting-delete"

    const val CREATE_ENDPOINT = "sampleGreetingCreateEndpoint"
    const val READ_ENDPOINT = "sampleGreetingReadEndpoint"
    const val UPDATE_ENDPOINT = "sampleGreetingUpdateEndpoint"
    const val DELETE_ENDPOINT = "sampleGreetingDeleteEndpoint"

    const val CREATE_TRANSPORT = "sampleGreetingCreateTransportClient"
    const val READ_TRANSPORT = "sampleGreetingReadTransportClient"
    const val UPDATE_TRANSPORT = "sampleGreetingUpdateTransportClient"
    const val DELETE_TRANSPORT = "sampleGreetingDeleteTransportClient"

    const val CREATE_CLIENT = "sampleGreetingCreateRestClient"
    const val READ_CLIENT = "sampleGreetingReadRestClient"
    const val UPDATE_CLIENT = "sampleGreetingUpdateRestClient"
    const val DELETE_CLIENT = "sampleGreetingDeleteRestClient"

    const val CREATE_ACTION = "create"
    const val READ_ACTION = "read"
    const val UPDATE_ACTION = "update"
    const val DELETE_ACTION = "delete"
}
