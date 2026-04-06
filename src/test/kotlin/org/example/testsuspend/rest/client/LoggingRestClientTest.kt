package org.example.testsuspend.rest.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest
import org.example.testsuspend.rest.logging.RestClientLogWriter

class LoggingRestClientTest {

    @Test
    fun `logs request and response around delegate call`() = runTest {
        val logWriter = TestLogWriter()
        val client = LoggingRestClient(
            operationName = "test-operation",
            delegate = object : RestClient<TestRequest, TestResponse> {
                override suspend fun call(request: TestRequest): TestResponse = TestResponse(status = "ok")
            },
            logWriter = logWriter,
            requestIdProvider = TestRequest::requestId,
        )

        val response = client.call(TestRequest(requestId = "req-1", customHeaders = emptyMap(), value = "hello"))

        assertEquals("ok", response.status)
        assertEquals(listOf("test-operation:req-1"), logWriter.requests)
        assertEquals(listOf("test-operation:req-1:ok"), logWriter.responses)
        assertEquals(emptyList(), logWriter.failures)
    }

    @Test
    fun `logs failure and rethrows delegate exception`() = runTest {
        val logWriter = TestLogWriter()
        val client = LoggingRestClient(
            operationName = "test-operation",
            delegate = object : RestClient<TestRequest, TestResponse> {
                override suspend fun call(request: TestRequest): TestResponse = error("boom")
            },
            logWriter = logWriter,
            requestIdProvider = TestRequest::requestId,
        )

        val exception = assertFailsWith<IllegalStateException> {
            client.call(TestRequest(requestId = "req-2", customHeaders = emptyMap(), value = "hello"))
        }

        assertEquals("boom", exception.message)
        assertEquals(listOf("test-operation:req-2"), logWriter.requests)
        assertEquals(emptyList(), logWriter.responses)
        assertEquals(listOf("test-operation:req-2:boom"), logWriter.failures)
    }
}

private data class TestRequest(
    override val requestId: String,
    override val customHeaders: Map<String, String>,
    val value: String,
) : RequestInfo

private data class TestResponse(
    val status: String,
)

private class TestLogWriter : RestClientLogWriter {
    val requests = mutableListOf<String>()
    val responses = mutableListOf<String>()
    val failures = mutableListOf<String>()

    override fun logRequest(operationName: String, requestId: String, request: Any) {
        requests += "$operationName:$requestId"
    }

    override fun logResponse(operationName: String, requestId: String, response: Any, durationMillis: Long) {
        responses += "$operationName:$requestId:${(response as TestResponse).status}"
    }

    override fun logFailure(operationName: String, requestId: String, durationMillis: Long, throwable: Throwable) {
        failures += "$operationName:$requestId:${throwable.message}"
    }
}
