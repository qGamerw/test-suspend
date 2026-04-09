package org.example.testsuspend.web

import kotlinx.coroutines.runBlocking
import org.example.testsuspend.rest.HttpRestClientDelegate
import org.example.testsuspend.rest.dto.Result
import org.example.testsuspend.rest.model.HttpBinResponse
import org.example.testsuspend.rest.model.PostmanEchoResponse
import org.example.testsuspend.rest.model.PublicPostRequest
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public-sender")
class PublicSenderController(
    @Qualifier("postmanEchoHttpRestClientDelegate")
    private val postmanEchoClient: HttpRestClientDelegate<PublicPostRequest, PostmanEchoResponse>,
    @Qualifier("httpBinHttpRestClientDelegate")
    private val httpBinClient: HttpRestClientDelegate<PublicPostRequest, HttpBinResponse>,
) {

    @PostMapping("/postman-echo")
    fun sendToPostmanEcho(
        @RequestBody(required = false) request: PublicPostRequest?,
    ): Result<PostmanEchoResponse> = runBlocking {
        postmanEchoClient.postCall(request ?: defaultRequest())
    }

    @PostMapping("/httpbin")
    fun sendToHttpBin(
        @RequestBody(required = false) request: PublicPostRequest?,
    ): Result<HttpBinResponse> = runBlocking {
        httpBinClient.postCall(request ?: defaultRequest())
    }

    private fun defaultRequest(): PublicPostRequest = PublicPostRequest(
        message = "hello-from-test-suspend",
        requestNumber = 1,
        active = true,
    )
}
