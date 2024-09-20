package com.jumso.config

import com.jumso.handler.WebSocketHandler
import com.jumso.util.DomainAddress
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@EnableWebSocket
@Configuration
class WebSocketConfig(
    private val domainAddress: DomainAddress,
    private val webSocketHandler: WebSocketHandler,
): WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(webSocketHandler, "/ws/chat").setAllowedOrigins(domainAddress.domainAddress)
    }

}