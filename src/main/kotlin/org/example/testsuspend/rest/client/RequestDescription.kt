package org.example.testsuspend.rest.client

import io.ktor.http.HttpMethod

data class RequestDescription<Request : java.io.Serializable>(
    val request: Request,
    val url: String,
    val method: HttpMethod,
    val integrationInfo: IntegrationInfo<Request>,
)
