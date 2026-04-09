package org.example.testsuspend.rest.model

data class PublicPostRequest(
    val message: String,
    val requestNumber: Int,
    val active: Boolean,
)

data class PostmanEchoResponse(
    val json: PublicPostRequest? = null,
    val url: String,
    val headers: Map<String, String> = emptyMap(),
)

data class HttpBinResponse(
    val json: PublicPostRequest? = null,
    val url: String,
    val headers: Map<String, String> = emptyMap(),
    val origin: String? = null,
)
