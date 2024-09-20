package com.jumso.handler

import com.example.jumso.domain.chat.repository.ChatRoomRepository
import com.jumso.registry.SessionRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.CloseStatus.NORMAL
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern

@Component
class WebSocketHandler(
    private val sessionRegistry: SessionRegistry,

    private val chatRoomRepository: ChatRoomRepository,

    private val redisTemplate: RedisTemplate<String, String>,

    @Value("\${server.port}")
    private val serverPort: String,
): TextWebSocketHandler() {
    private val memberIdToServerPortAndSessionId = "member-id-to-server-port-and-session-id"
    private val chatServerLoad = "chat-server-load"

    // 클라이언트와 연결이 맺어진 후
    override fun afterConnectionEstablished(session: WebSocketSession) {
        super.afterConnectionEstablished(session)

        // session에서 memberId를 가져온다.
        session.attributes["memberId"]
            ?.let {
                val memberId = it as Long

                // sessionRegistry에 session을 추가한다.
                sessionRegistry.addSession(session)

                // redis에 memberId를 키로 현재 서버의 포트와 session의 id를 값으로 저장한다.
                // TTL은 1일
                redisTemplate.opsForHash<Long, String>().put(
                    memberIdToServerPortAndSessionId,
                    memberId,
                    "${now().format(ofPattern("yyyy-MM-dd"))}-${serverPort}-${session.id}"
                )

                // redis에 "chat-server-load"로 ws 개수 보고
                redisTemplate.opsForZSet().add(chatServerLoad, serverPort, sessionRegistry.getSessionCount())

                // ToDo: 채팅방 목록 조회하여 클라이언트에게 전달
                chatRoomRepository.

                session.sendMessage(
            }
            ?: run {
                // memberId가 없으면 연결을 끊는다.
                session.close(NORMAL.withReason("memberId가 없습니다."))
            }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val payload = message.payload
        session.sendMessage(TextMessage("Hello, $payload"))
    }

    // 통신 에러시
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        super.handleTransportError(session, exception)

        // sessionRegistry에서 session을 제거한다.
        sessionRegistry.removeSession(session)

        // redis에서 memberId를 키로 현재 서버의 포트와 session의 id를 삭제한다.
        redisTemplate.opsForHash<Long, String>().delete(memberIdToServerPortAndSessionId, session.attributes["memberId"])

        // redis에 "chat-server-load"로 ws 개수 보고
        redisTemplate.opsForZSet().add(chatServerLoad, serverPort, sessionRegistry.getSessionCount())
    }

    // 연결 종료시
    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        super.afterConnectionClosed(session, closeStatus)

        // sessionRegistry에서 session을 제거한다.
        sessionRegistry.removeSession(session)

        // redis에서 memberId를 키로 현재 서버의 포트와 session의 id를 삭제한다.
        redisTemplate.opsForHash<Long, String>().delete(memberIdToServerPortAndSessionId, session.attributes["memberId"])

        // redis에 "chat-server-load"로 ws 개수 보고
        redisTemplate.opsForZSet().add(chatServerLoad, serverPort, sessionRegistry.getSessionCount())
    }
}