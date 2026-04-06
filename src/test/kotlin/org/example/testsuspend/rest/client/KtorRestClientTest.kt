package org.example.testsuspend.rest.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.headersOf
import io.ktor.serialization.jackson.jackson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest
import org.example.testsuspend.rest.config.HttpClientSettings
import org.example.testsuspend.rest.config.HttpConfiguration
import org.example.testsuspend.rest.config.KtorHttpClientFactory

class KtorRestClientTest {

    @Test
    fun `sends json request and parses json response`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedUuidHeader: String? = null
        var capturedCustomHeader: String? = null

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedUuidHeader = request.headers["X-Request-Id"]
            capturedCustomHeader = request.headers["X-Custom-Key"]

            respond(
                content = """{"message":"hello"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        val client = testClient(engine)

        val response = client.call(
            GreetingRequest(
                name = "test",
                requestId = "123e4567-e89b-12d3-a456-426614174000",
                customHeaders = mapOf("X-Custom-Key" to "custom-value"),
            ),
        )

        assertEquals(HttpMethod.Post, capturedMethod)
        assertEquals("123e4567-e89b-12d3-a456-426614174000", capturedUuidHeader)
        assertEquals("custom-value", capturedCustomHeader)
        assertEquals("hello", response.message)
    }

    @Test
    fun `maps non-success status to http exception`() = runTest {
        val client = testClient(
            MockEngine {
                respond(
                    content = "upstream-error",
                    status = InternalServerError,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString()),
                )
            },
        )

        val exception = assertFailsWith<RestClientHttpException> {
            client.call(testRequest())
        }

        assertEquals(500, exception.statusCode.value)
        assertEquals("upstream-error", exception.responseBody)
        assertEquals("req-1", exception.requestId)
    }

    @Test
    fun `maps invalid response body to serialization exception`() = runTest {
        val client = testClient(
            MockEngine {
                respond(
                    content = "not-json",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        val exception = assertFailsWith<RestClientSerializationException> {
            client.call(testRequest())
        }

        assertEquals("req-1", exception.requestId)
    }

    @Test
    fun `maps timeout to timeout exception`() = runTest {
        val client = testClient(
            MockEngine {
                throw HttpRequestTimeoutException("https://example.org/test", 10_000)
            },
        )

        val exception = assertFailsWith<RestClientTimeoutException> {
            client.call(testRequest())
        }

        assertEquals("req-1", exception.requestId)
    }

    private fun testClient(engine: MockEngine): KtorRestClient<GreetingRequest, GreetingResponse> = KtorRestClient(
        configuration = HttpConfiguration(
            url = "https://example.org/test",
            requestIdHeaderName = "X-Request-Id",
            initialSettings = HttpClientSettings(
                threadCount = 16,
                timeoutMillis = 10_000,
                maxConnectionsCount = 64,
                maxConnectionsPerRoute = 16,
                keepAliveTimeMillis = 5_000,
                connectAttempts = 1,
            ),
        ),
        httpClientFactory = object : KtorHttpClientFactory() {
            override fun create(settings: HttpClientSettings): HttpClient = HttpClient(engine) {
                install(ContentNegotiation) {
                    jackson {
                        findAndRegisterModules()
                    }
                }
            }
        },
        serializer = TypeInfoSerializable(GreetingRequest::class, GreetingResponse::class),
        integrationInfo = IntegrationInfo(
            name = "test-integration",
            requestIdProvider = GreetingRequest::requestId,
            customHeadersProvider = GreetingRequest::customHeaders,
        ),
    )

    private fun testRequest() = GreetingRequest(
        name = "test",
        requestId = "req-1",
        customHeaders = mapOf("X-Custom-Key" to "custom-value"),
    )
}

private data class GreetingRequest(
    val name: String,
    override val requestId: String,
    override val customHeaders: Map<String, String>,
) : RequestInfo, java.io.Serializable

private data class GreetingResponse(
    val message: String,
)
