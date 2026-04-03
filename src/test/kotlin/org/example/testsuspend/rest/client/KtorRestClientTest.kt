package org.example.testsuspend.rest.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.content.OutgoingContent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorRestClientTest {

    @Test
    fun `sends json request and parses json response`() = runTest {
        var capturedBody: String? = null
        var capturedMethod: HttpMethod? = null
        var capturedUuidHeader: String? = null
        var capturedCustomHeader: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedBody = (request.body as OutgoingContent.ByteArrayContent).bytes().decodeToString()
            capturedUuidHeader = request.headers["X-Request-Id"]
            capturedCustomHeader = request.headers["X-Custom-Key"]

            respond(
                content = """{"message":"hello"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val client = KtorRestClient(
            httpClient = HttpClient(engine) {
                install(ContentNegotiation) {
                    json(Json)
                }
            },
            url = "https://example.org/test",
            requestType = GreetingRequest::class,
            responseType = GreetingResponse::class,
            uuidHeaderName = "X-Request-Id",
            uuidProvider = GreetingRequest::requestId,
            customHeadersProvider = GreetingRequest::customHeaders,
        )

        val response = client.call(
            GreetingRequest(
                name = "test",
                requestId = "123e4567-e89b-12d3-a456-426614174000",
                customHeaders = mapOf("X-Custom-Key" to "custom-value"),
            ),
        )

        assertEquals(HttpMethod.Post, capturedMethod)
        assertEquals("{\"name\":\"test\",\"requestId\":\"123e4567-e89b-12d3-a456-426614174000\",\"customHeaders\":{\"X-Custom-Key\":\"custom-value\"}}", capturedBody)
        assertEquals("123e4567-e89b-12d3-a456-426614174000", capturedUuidHeader)
        assertEquals("custom-value", capturedCustomHeader)
        assertEquals("hello", response.message)
    }
}

@Serializable
private data class GreetingRequest(
    val name: String,
    val requestId: String,
    val customHeaders: Map<String, String>,
)

@Serializable
private data class GreetingResponse(
    val message: String,
)
