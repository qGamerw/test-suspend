package org.example.testsuspend.sample.controller

import org.example.testsuspend.sample.service.impl.SampleGreetingUpdateService
import org.example.testsuspend.utils.SampleGreetingPayload
import org.example.testsuspend.utils.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Endpoint обновления greeting-сущности.
 */
@RestController
@RequestMapping("/api/sample/greetings")
class SampleGreetingUpdateController(
    private val sampleGreetingUpdateService: SampleGreetingUpdateService,
) {

    @PutMapping("/{name}")
    fun update(
        @PathVariable name: String,
        @RequestBody request: SampleGreetingPayload,
    ): ResponseEntity<Any> = sampleGreetingUpdateService.updateGreeting(request.name.ifBlank { name }).toResponse()
}
