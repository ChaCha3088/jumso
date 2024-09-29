package kr.co.jumso.config

import kr.co.jumso.handler.WebSocketHandler
import kr.co.jumso.interceptor.JwtHandShakeInterceptor
import kr.co.jumso.util.DomainAddress
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@EnableWebSocket
@Configuration
class WebSocketConfig(
    private val domainAddress: DomainAddress,
    private val webSocketHandler: WebSocketHandler,
    private val jwtHandShakeInterceptor: JwtHandShakeInterceptor,
): WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(webSocketHandler, "/ws/chat").setAllowedOrigins(domainAddress.domainAddress)
            .addInterceptors(jwtHandShakeInterceptor)
    }
}
