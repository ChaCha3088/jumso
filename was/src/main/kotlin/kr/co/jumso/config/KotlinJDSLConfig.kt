package kr.co.jumso.config

import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KotlinJDSLConfig {
    @Bean
    fun context() = JpqlRenderContext()

    @Bean
    fun renderer() = JpqlRenderer()
}
