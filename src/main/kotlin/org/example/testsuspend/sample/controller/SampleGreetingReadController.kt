package org.example.testsuspend.sample.controller

import org.example.testsuspend.sample.service.impl.SampleGreetingReadService
import org.example.testsuspend.utils.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Endpoint чтения greeting-сущности.
 */
@RestController
@RequestMapping("/api/sample/greetings")
class SampleGreetingReadController(
    private val sampleGreetingReadService: SampleGreetingReadService,
) {

    @GetMapping("/{name}")
    fun read(@PathVariable name: String): ResponseEntity<Any> =
        sampleGreetingReadService.getGreeting(name).toResponse()
}
