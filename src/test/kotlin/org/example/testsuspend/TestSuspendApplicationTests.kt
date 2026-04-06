package org.example.testsuspend

import kotlin.test.assertNotNull
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.config.KtorHttpClientFactory
import org.example.testsuspend.rest.controller.RestClientUsageExamplesController
import org.example.testsuspend.rest.logging.RestClientLogWriter
import org.example.testsuspend.rest.model.CreatePaymentRequest
import org.example.testsuspend.rest.model.CreatePaymentResponse
import org.example.testsuspend.rest.model.ResolveCustomerRequest
import org.example.testsuspend.rest.model.ResolveCustomerResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestSuspendApplicationTests {

    @Autowired
    private lateinit var httpClientFactory: KtorHttpClientFactory

    @Autowired
    private lateinit var restClientLogWriter: RestClientLogWriter

    @Autowired
    private lateinit var restClientUsageExamplesController: RestClientUsageExamplesController

    @Autowired
    @Qualifier("createPaymentRestClient")
    private lateinit var createPaymentRestClient: RestClient<CreatePaymentRequest, CreatePaymentResponse>

    @Autowired
    @Qualifier("resolveCustomerRestClient")
    private lateinit var resolveCustomerRestClient: RestClient<ResolveCustomerRequest, ResolveCustomerResponse>

    @Test
    fun contextLoads() {
        assertNotNull(httpClientFactory)
        assertNotNull(restClientLogWriter)
        assertNotNull(restClientUsageExamplesController)
        assertNotNull(createPaymentRestClient)
        assertNotNull(resolveCustomerRestClient)
    }

}
