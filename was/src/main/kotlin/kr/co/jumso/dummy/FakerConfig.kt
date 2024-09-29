package kr.co.jumso.dummy

import io.github.serpro69.kfaker.Faker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FakerConfig {
    @Bean
    fun faker(): Faker {
        return Faker()
    }
}
