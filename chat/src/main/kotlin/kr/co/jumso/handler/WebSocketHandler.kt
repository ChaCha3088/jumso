package kr.co.jumso.handler

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.jumso.annotation.Valid
import kr.co.jumso.chat.service.ChatRoomService
import kr.co.jumso.domain.chat.dto.CreateChatRoomRequest
import kr.co.jumso.domain.chat.dto.Message
import kr.co.jumso.domain.chat.enumstorage.MessageType.*
import kr.co.jumso.registry.SessionRegistry
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
    private val chatRoomService: ChatRoomService,

    private val sessionRegistry: SessionRegistry,

    private val redisTemplate: RedisTemplate<String, String>,

    private val objectMapper: ObjectMapper,

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
                val memberId = it as String

                // sessionRegistry에 session을 추가한다.
                sessionRegistry.addSession(session)

                // redis에 memberId를 키로 현재 서버의 포트와 session의 id를 값으로 저장한다.
                // TTL은 1일
                redisTemplate.opsForHash<String, String>().put(
                    memberIdToServerPortAndSessionId,
                    memberId,
                    "${now().format(ofPattern("yyyy-MM-dd"))} $serverPort ${session.id}"
                )

                // redis에 "chat-server-load"로 ws 개수 보고
                redisTemplate.opsForZSet().add(chatServerLoad, serverPort, sessionRegistry.getSessionCount())

                // 채팅방 목록을 조회하여 클라이언트에게 전달
                val result = objectMapper.writeValueAsString(chatRoomService.findChatRoomByMemberId(memberId.toLong()))
                
                session.sendMessage(TextMessage(result))
            }
            ?: run {
                // memberId가 없으면 연결을 끊는다.
                session.close(NORMAL.withReason("memberId가 없습니다."))
            }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // Message 객체로 변환
        val payload = message.payload

        val messageObject = objectMapper.readValue(payload, Message::class.java)

        // ToDo: MessageType에 따라 분기처리 예정
        when (messageObject.type) {
            // 채팅방 목록 조회
            REQUEST_SELECT_CHAT_ROOM_LIST -> {
                // session에서 memberId를 가져온다.
                session.attributes["memberId"]
                    ?.let {
                        val memberId = it as Long

                        // 채팅방 목록을 조회하여 클라이언트에게 전달
                        val result = objectMapper.writeValueAsString(chatRoomService.findChatRoomByMemberId(memberId))

                        session.sendMessage(TextMessage(result))
                    }
                    ?: run {
                        // memberId가 없으면 연결을 끊는다.
                        session.close(NORMAL.withReason("memberId가 없습니다."))
                    }
            }
            // 채팅방 생성
            REQUEST_CREATE_CHAT_ROOM -> {
                // message에서 CreateChatRoomRequest를 가져온다.
                val createChatRoomRequest = objectMapper.convertValue(messageObject.data, CreateChatRoomRequest::class.java)

                // session에서 memberId를 가져온다.
                session.attributes["memberId"]
                    ?.let {
                        val memberId = it as Long

                        // 채팅방 생성 요청
                        val result = objectMapper.writeValueAsString(chatRoomService.createChatRoom(memberId, @Valid createChatRoomRequest))
                    }

            }
            // 채팅방 삭제
            REQUEST_DELETE_CHAT_ROOM -> {

            }
            // 채팅방의 메시지 조회
            REQUEST_SELECT_CHAT_ROOM_MESSAGES -> {

            }
            // 채팅 보내기
            REQUEST_SEND_CHAT -> {

            }
            else -> {
                // 예외 처리
            }
        }
    }

    // 통신 에러시
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        super.handleTransportError(session, exception)

        // sessionRegistry에서 session을 제거한다.
        sessionRegistry.removeSession(session)

        // redis에서 memberId를 키로 현재 서버의 포트와 session의 id를 삭제한다.
        redisTemplate.opsForHash<String, String>().delete(memberIdToServerPortAndSessionId, session.attributes["memberId"])

        // redis에 "chat-server-load"로 ws 개수 보고
        redisTemplate.opsForZSet().add(chatServerLoad, serverPort, sessionRegistry.getSessionCount())
    }

    // 연결 종료시
    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        super.afterConnectionClosed(session, closeStatus)

        // sessionRegistry에서 session을 제거한다.
        sessionRegistry.removeSession(session)

        // redis에서 memberId를 키로 현재 서버의 포트와 session의 id를 삭제한다.
        redisTemplate.opsForHash<String, String>().delete(memberIdToServerPortAndSessionId, session.attributes["memberId"])

        // redis에 "chat-server-load"로 ws 개수 보고
        redisTemplate.opsForZSet().add(chatServerLoad, serverPort, sessionRegistry.getSessionCount())
    }
}
