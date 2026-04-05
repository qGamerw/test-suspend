package org.example.testsuspend.rest.controller

import kotlinx.coroutines.runBlocking
import org.example.testsuspend.rest.client.RestClient
import org.example.testsuspend.rest.model.CreatePaymentRequest
import org.example.testsuspend.rest.model.CreatePaymentResponse
import org.example.testsuspend.rest.model.ResolveCustomerRequest
import org.example.testsuspend.rest.model.ResolveCustomerResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@RestController
@RequestMapping("/api/examples/rest-client")
class RestClientUsageExamplesController(
    @Qualifier("createPaymentRestClient")
    private val createPaymentRestClient: RestClient<CreatePaymentRequest, CreatePaymentResponse>,
    @Qualifier("resolveCustomerRestClient")
    private val resolveCustomerRestClient: RestClient<ResolveCustomerRequest, ResolveCustomerResponse>,
    @Qualifier("legacyRestExecutor")
    private val legacyRestExecutor: Executor,
) {

    @PostMapping("/payment-proxy")
    // Пример простого proxy-эндпоинта: контроллер сразу пробрасывает запрос во внешний сервис.
    suspend fun paymentProxy(
        @RequestBody request: PaymentProxyHttpRequest,
    ): CreatePaymentResponse = createPaymentRestClient.call(
        CreatePaymentRequest(
            requestId = request.requestId,
            customHeaders = request.customHeaders,
            merchantId = request.merchantId,
            orderId = request.orderId,
            amountMinor = request.amountMinor,
            currency = request.currency,
        ),
    )

    @PostMapping("/legacy-customer-summary")
    // Пример для легаси: старый код ждёт CompletableFuture, а внутри уже используется новый suspend-клиент.
    fun legacyCustomerSummary(
        @RequestBody request: LegacyCustomerSummaryHttpRequest,
    ): CompletableFuture<LegacyCustomerSummaryHttpResponse> {
        val paymentFuture = CompletableFuture.supplyAsync({
            runBlocking {
                createPaymentRestClient.call(
                    CreatePaymentRequest(
                        requestId = request.requestId,
                        customHeaders = request.customHeaders,
                        merchantId = request.merchantId,
                        orderId = request.orderId,
                        amountMinor = request.amountMinor,
                        currency = request.currency,
                    )
                )
            }
        }, legacyRestExecutor)

        val customerFuture = CompletableFuture.supplyAsync({
            runBlocking {
                resolveCustomerRestClient.call(
                    ResolveCustomerRequest(
                        requestId = request.requestId,
                        customHeaders = request.customHeaders,
                        customerId = request.customerId,
                        locale = request.locale,
                        includeContacts = request.includeContacts,
                    )
                )
            }
        }, legacyRestExecutor)

        return paymentFuture.thenCombine(customerFuture) { payment, customer ->
            LegacyCustomerSummaryHttpResponse(
                requestId = request.requestId,
                paymentId = payment.paymentId,
                paymentStatus = payment.status,
                approved = payment.approved,
                customerId = customer.customerId,
                customerName = customer.fullName,
                customerTier = customer.tier,
            )
        }
    }
}

data class PaymentProxyHttpRequest(
    val requestId: String,
    val customHeaders: Map<String, String> = emptyMap(),
    val merchantId: String,
    val orderId: String,
    val amountMinor: Long,
    val currency: String,
)

data class LegacyCustomerSummaryHttpRequest(
    val requestId: String,
    val customHeaders: Map<String, String> = emptyMap(),
    val merchantId: String,
    val orderId: String,
    val amountMinor: Long,
    val currency: String,
    val customerId: String,
    val locale: String,
    val includeContacts: Boolean,
)

data class LegacyCustomerSummaryHttpResponse(
    val requestId: String,
    val paymentId: String,
    val paymentStatus: String,
    val approved: Boolean,
    val customerId: String,
    val customerName: String,
    val customerTier: String,
)
