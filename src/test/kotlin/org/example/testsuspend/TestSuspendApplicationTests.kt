package org.example.testsuspend

import io.ktor.client.HttpClient
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull

@SpringBootTest
class TestSuspendApplicationTests {

    @Autowired
    private lateinit var httpClient: HttpClient

    @Test
    fun contextLoads() {
        assertNotNull(httpClient)
    }

}
