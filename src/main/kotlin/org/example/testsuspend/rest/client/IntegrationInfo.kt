package org.example.testsuspend.rest.client

data class IntegrationInfo<Request : java.io.Serializable>(
    val name: String,
    val requestIdProvider: (Request) -> String,
    val customHeadersProvider: (Request) -> Map<String, String> = { emptyMap() },
)
