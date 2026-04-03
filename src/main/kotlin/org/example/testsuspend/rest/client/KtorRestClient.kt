package org.example.testsuspend.rest.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.TypeInfo
import kotlin.reflect.KClass

class KtorRestClient<Rq : Any, Rs : Any>(
    private val httpClient: HttpClient,
    private val url: String,
    private val requestType: KClass<Rq>,
    private val responseType: KClass<Rs>,
    private val uuidHeaderName: String,
    private val uuidProvider: (Rq) -> String,
    private val customHeadersProvider: (Rq) -> Map<String, String> = { emptyMap() },
    private val httpMethod: HttpMethod = HttpMethod.Post,
    private val configureRequest: HttpRequestBuilder.() -> Unit = {},
) : RestClient<Rq, Rs> {

    override suspend fun call(request: Rq): Rs {
        val response = httpClient.request(url) {
            method = httpMethod
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            header(uuidHeaderName, uuidProvider(request))
            customHeadersProvider(request).forEach { (key, value) ->
                header(key, value)
            }
            setBody(request, TypeInfo(requestType, requestType.java, null))
            configureRequest()
        }

        return response.body(TypeInfo(responseType, responseType.java, null))
    }
}
