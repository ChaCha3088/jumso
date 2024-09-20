package kr.co.jumso.config

import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper() = jacksonObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
            .configure(WRITE_DATES_AS_TIMESTAMPS, false)
            .registerModules(JavaTimeModule())
    }
}
