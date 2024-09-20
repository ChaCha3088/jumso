package com.jumso.interceptor

import com.example.jumso.domain.auth.service.JwtService
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

@Component
class JwtHandShakeInterceptor(
    private val jwtService: JwtService,
): HandshakeInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        // request의 header에서 jwt를 가져온다.
        var authorization = request.headers["Authorization"].toString()

        authorization = authorization.replace("Bearer ", "")

        // 클라이언트의 jwt를 검증하고, memberId를 가져온다.
        val memberId = jwtService.extractMemberIdFromAccessToken(authorization)

        // attributes에 memberId를 저장한다.
        attributes["memberId"] = memberId

        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {}
}