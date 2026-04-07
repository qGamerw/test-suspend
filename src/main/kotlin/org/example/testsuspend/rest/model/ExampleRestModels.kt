package org.example.testsuspend.rest.model

data class CreatePaymentRequest(
    val merchantId: String,
    val orderId: String,
    val amountMinor: Long,
    val currency: String,
)

data class CreatePaymentResponse(
    val paymentId: String,
    val status: String,
    val approved: Boolean,
)

data class ResolveCustomerRequest(
    val customerId: String,
    val locale: String,
    val includeContacts: Boolean,
)

data class ResolveCustomerResponse(
    val customerId: String,
    val fullName: String,
    val tier: String,
)
