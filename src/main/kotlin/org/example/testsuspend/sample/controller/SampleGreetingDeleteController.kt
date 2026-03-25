package org.example.testsuspend.sample.controller

import org.example.testsuspend.sample.service.impl.SampleGreetingDeleteService
import org.example.testsuspend.utils.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Endpoint удаления greeting-сущности.
 */
@RestController
@RequestMapping("/api/sample/greetings")
class SampleGreetingDeleteController(
    private val sampleGreetingDeleteService: SampleGreetingDeleteService,
) {

    @DeleteMapping("/{name}")
    fun delete(@PathVariable name: String): ResponseEntity<Any> =
        sampleGreetingDeleteService.deleteGreeting(name).toResponse()
}
