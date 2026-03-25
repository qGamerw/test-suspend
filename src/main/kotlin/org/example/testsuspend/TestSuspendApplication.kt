package org.example.testsuspend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TestSuspendApplication

fun main(args: Array<String>) {
    runApplication<TestSuspendApplication>(*args)
}
