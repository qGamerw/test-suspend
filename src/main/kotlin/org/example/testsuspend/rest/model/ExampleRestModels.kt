package org.example.testsuspend.rest.model

import org.example.testsuspend.rest.client.RequestInfo

data class CreatePaymentRequest(
    override val requestId: String,
    override val customHeaders: Map<String, String> = emptyMap(),
    val merchantId: String,
    val orderId: String,
    val amountMinor: Long,
    val currency: String,
) : RequestInfo

data class CreatePaymentResponse(
    val paymentId: String,
    val status: String,
    val approved: Boolean,
)

data class ResolveCustomerRequest(
    override val requestId: String,
    override val customHeaders: Map<String, String> = emptyMap(),
    val customerId: String,
    val locale: String,
    val includeContacts: Boolean,
) : RequestInfo

data class ResolveCustomerResponse(
    val customerId: String,
    val fullName: String,
    val tier: String,
)
