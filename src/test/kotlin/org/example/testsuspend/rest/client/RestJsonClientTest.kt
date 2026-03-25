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
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import org.example.testsuspend.rest.client.decorator.LoggingRestJsonClientDecorator
import org.example.testsuspend.rest.client.decorator.MappingRestJsonClientDecorator
import org.example.testsuspend.rest.client.decorator.MetricsRestJsonClientDecorator
import org.example.testsuspend.rest.exception.ResponseMappingException
import org.example.testsuspend.rest.logging.RestClientLogWriter
import org.example.testsuspend.rest.logging.RestMetricEvent
import org.example.testsuspend.rest.mapper.KotlinxRestResponseMapper
import org.example.testsuspend.rest.model.RawRestResponse
import org.example.testsuspend.rest.model.RestCallResult
import org.example.testsuspend.rest.model.RestEndpoint
import org.example.testsuspend.rest.model.RestRequest

class RestJsonClientTest {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Test
    fun `maps successful response`() = runTest {
        val mappedClient = mappedClientReturning(
            content = """{"message":"hello"}""",
            status = HttpStatusCode.OK,
        )

        val result = mappedClient.execute(sampleRequest())

        val success = assertIs<RestCallResult.Success<GreetingResponse>>(result)
        assertEquals(200, success.status)
        assertEquals("hello", success.body.message)
    }

    @Test
    fun `maps error response into dto`() = runTest {
        val mappedClient = mappedClientReturning(
            content = """{"code":"bad_request","message":"invalid input"}""",
            status = HttpStatusCode.BadRequest,
        )

        val result = mappedClient.execute(sampleRequest())

        val failure = assertIs<RestCallResult.Failure<ApiError>>(result)
        assertEquals(400, failure.status)
        assertEquals("bad_request", failure.error?.code)
        assertEquals("invalid input", failure.error?.message)
    }

    @Test
    fun `returns null error dto for blank error body`() = runTest {
        val mappedClient = mappedClientReturning(
            content = "",
            status = HttpStatusCode.InternalServerError,
        )

        val result = mappedClient.execute(sampleRequest())

        val failure = assertIs<RestCallResult.Failure<ApiError>>(result)
        assertNull(failure.error)
        assertNull(failure.rawBody)
    }

    @Test
    fun `throws mapping exception for invalid success json`() = runTest {
        val mappedClient = mappedClientReturning(
            content = """{"unknown":"value"}""",
            status = HttpStatusCode.OK,
        )

        assertFailsWith<ResponseMappingException> {
            mappedClient.execute(sampleRequest())
        }
    }

    @Test
    fun `logging and metrics decorators capture request lifecycle`() = runTest {
        val logWriter = TestLogWriter()
        val endpoint = sampleEndpoint()
        val rawClient = MetricsRestJsonClientDecorator(
            delegate = LoggingRestJsonClientDecorator(
                delegate = BaseRestJsonClient(httpClientReturning("""{"message":"hello"}""", HttpStatusCode.OK), endpoint),
                logWriter = logWriter,
                endpoint = endpoint,
            ),
            logWriter = logWriter,
            endpoint = endpoint,
        )

        val request = sampleRequest()
        val response = rawClient.execute(request)

        assertEquals(200, response.status)
        assertEquals(1, logWriter.requests.size)
        assertEquals(1, logWriter.responses.size)
        assertEquals(1, logWriter.metrics.size)
        assertEquals("success", logWriter.metrics.single().outcome)
        assertEquals(endpoint.operationName, logWriter.requests.single().first.operationName)
    }

    private fun mappedClientReturning(content: String, status: HttpStatusCode): RestClient<GreetingResponse, ApiError> {
        val rawClient = MetricsRestJsonClientDecorator(
            delegate = LoggingRestJsonClientDecorator(
                delegate = BaseRestJsonClient(httpClientReturning(content, status), sampleEndpoint()),
                logWriter = TestLogWriter(),
                endpoint = sampleEndpoint(),
            ),
            logWriter = TestLogWriter(),
            endpoint = sampleEndpoint(),
        )
        return MappingRestJsonClientDecorator(
            delegate = rawClient,
            endpoint = sampleEndpoint(),
            responseMapper = KotlinxRestResponseMapper(json),
            successDeserializer = GreetingResponse.serializer(),
            errorDeserializer = ApiError.serializer(),
        )
    }

    private fun httpClientReturning(content: String, status: HttpStatusCode): HttpClient {
        val engine = MockEngine {
            respond(
                content = content,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

        return HttpClient(engine) {
            expectSuccess = false
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    private fun sampleRequest(): RestRequest = RestRequest(
        body = """{"name":"test"}""",
    )

    private fun sampleEndpoint(): RestEndpoint = RestEndpoint(
        operationName = "test-operation",
        method = HttpMethod.Post,
        url = "https://example.org/test",
        headers = mapOf(HttpHeaders.Accept to ContentType.Application.Json.toString()),
    )
}

private class TestLogWriter : RestClientLogWriter {
    val requests = mutableListOf<Pair<RestEndpoint, RestRequest>>()
    val responses = mutableListOf<Triple<RestEndpoint, RestRequest, RawRestResponse>>()
    val failures = mutableListOf<Triple<RestEndpoint, RestRequest, Throwable>>()
    val metrics = mutableListOf<RestMetricEvent>()

    override fun logRequest(endpoint: RestEndpoint, request: RestRequest) {
        requests += endpoint to request
    }

    override fun logResponse(endpoint: RestEndpoint, request: RestRequest, response: RawRestResponse, durationMillis: Long) {
        responses += Triple(endpoint, request, response)
    }

    override fun logFailure(endpoint: RestEndpoint, request: RestRequest, durationMillis: Long, throwable: Throwable) {
        failures += Triple(endpoint, request, throwable)
    }

    override fun logMetric(event: RestMetricEvent) {
        metrics += event
    }
}

@Serializable
private data class GreetingResponse(
    val message: String,
)

@Serializable
private data class ApiError(
    val code: String,
    val message: String,
)
