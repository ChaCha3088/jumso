package kr.co.jumso.interceptor

import kr.co.jumso.domain.auth.service.JwtService
import kr.co.jumso.enumstorage.RedisKeys.MEMBER_ID_TO_SERVER_PORT
import kr.co.jumso.registry.SessionRegistry
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatusCode
import org.springframework.http.HttpStatusCode.valueOf
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.CloseStatus.NORMAL
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

@Component
class JwtHandShakeInterceptor(
    private val jwtService: JwtService,

    private val redisTemplate: RedisTemplate<String, Any>,

    private val sessionRegistry: SessionRegistry,
): HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        // request의 header에서 jwt를 가져온다.
        request.headers["Authorization"]?.firstOrNull()
            ?.let {
                if (!it.contains("Bearer ")) {
                    return false
                }

                val accessToken = it.replace("Bearer ", "")

                // 클라이언트의 jwt를 검증하고, memberId를 가져온다.
                val memberId = runCatching {
                    jwtService.extractMemberIdFromAccessToken(accessToken)
                }.getOrElse {
                    response.setStatusCode(valueOf(401))

                    // "JWT 토큰이 유효하지 않습니다."
                    response.body.write("JWT 토큰이 유효하지 않습니다.".toByteArray())

                    return false
                }

                // sessionRegistry에 이미 있는지 확인한다.
                if (sessionRegistry.containsSession(memberId)) {
                    // 원래 세션 종료
                    sessionRegistry.getSession(memberId)?.close(NORMAL)
                }

                // attributes에 memberId를 저장한다.
                attributes["memberId"] = memberId.toString()

                return true
            }

        // jwt가 없으면
        response.setStatusCode(valueOf(401))

        // "JWT 토큰이 없습니다."
        response.body.write("JWT 토큰이 없습니다.".toByteArray())

        return false
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {}
}
