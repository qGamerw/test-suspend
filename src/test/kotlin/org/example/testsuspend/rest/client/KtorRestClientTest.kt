package org.example.testsuspend.rest.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.content.OutgoingContent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorRestClientTest {

    @Test
    fun `sends json request and parses json response`() = runTest {
        var capturedBody: String? = null
        var capturedMethod: HttpMethod? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedBody = (request.body as OutgoingContent.ByteArrayContent).bytes().decodeToString()

            respond(
                content = """{"message":"hello"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val client = KtorRestClient(
            httpClient = HttpClient(engine),
            url = "https://example.org/test",
            requestSerializer = GreetingRequest.serializer(),
            responseSerializer = GreetingResponse.serializer(),
        )

        val response = client.call(GreetingRequest(name = "test"))

        assertEquals(HttpMethod.Post, capturedMethod)
        assertEquals("{\"name\":\"test\"}", capturedBody)
        assertEquals("hello", response.message)
    }
}

@Serializable
private data class GreetingRequest(
    val name: String,
)

@Serializable
private data class GreetingResponse(
    val message: String,
)
