package org.example.testsuspend.sample.controller

import org.example.testsuspend.sample.service.impl.SampleGreetingCreateService
import org.example.testsuspend.utils.SampleGreetingPayload
import org.example.testsuspend.utils.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Endpoint создания greeting-сущности.
 */
@RestController
@RequestMapping("/api/sample/greetings")
class SampleGreetingCreateController(
    private val sampleGreetingCreateService: SampleGreetingCreateService,
) {

    @PostMapping
    fun create(@RequestBody request: SampleGreetingPayload): ResponseEntity<Any> =
        sampleGreetingCreateService.createGreeting(request.name).toResponse()
}
