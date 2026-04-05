package org.example.testsuspend.rest.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
class UsageExampleConfig {

    @Bean("legacyRestExecutor")
    // Небольшой bounded pool показывает, как в легаси-коде не разрастись в бесконтрольный parallelism.
    fun legacyRestExecutor(): Executor = ThreadPoolTaskExecutor().apply {
        corePoolSize = 4
        maxPoolSize = 4
        queueCapacity = 32
        setThreadNamePrefix("legacy-rest-")
        initialize()
    }
}
