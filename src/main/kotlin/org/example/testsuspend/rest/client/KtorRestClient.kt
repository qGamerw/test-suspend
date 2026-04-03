package org.example.testsuspend.rest.client

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.TextContent
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class KtorRestClient<Rq : Any, Rs : Any>(
    private val httpClient: HttpClient,
    private val url: String,
    private val requestSerializer: KSerializer<Rq>,
    private val responseSerializer: KSerializer<Rs>,
    private val httpMethod: HttpMethod = HttpMethod.Post,
    private val json: Json = Json,
    private val configureRequest: HttpRequestBuilder.() -> Unit = {},
) : RestClient<Rq, Rs> {

    override suspend fun call(request: Rq): Rs {
        val response = httpClient.request(url) {
            method = httpMethod
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            setBody(
                TextContent(
                    text = json.encodeToString(requestSerializer, request),
                    contentType = ContentType.Application.Json,
                ),
            )
            configureRequest()
        }

        return json.decodeFromString(responseSerializer, response.bodyAsText())
    }
}
