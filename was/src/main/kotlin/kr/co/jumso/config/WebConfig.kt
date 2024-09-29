package kr.co.jumso.config

import kr.co.jumso.domain.auth.resolver.MemberIdResolver
import kr.co.jumso.domain.auth.service.JwtService
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val jwtService: JwtService,
): WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver?>) {
        resolvers.add(MemberIdResolver(jwtService))
    }
}
