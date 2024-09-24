package kr.co.jumso.handler

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.jumso.chat.service.ChatRoomService
import kr.co.jumso.domain.chat.dto.CreateChatRoomRequest
import kr.co.jumso.domain.chat.dto.DeleteChatRoomRequest
import kr.co.jumso.domain.chat.dto.RequestMessage
import kr.co.jumso.domain.chat.dto.ResponseMessage
import kr.co.jumso.domain.chat.enumstorage.MessageStatus.SUCCESS
import kr.co.jumso.domain.chat.enumstorage.MessageType.*
import kr.co.jumso.enumstorage.RedisKeys.MEMBER_ID_TO_SERVER_PORT_AND_SESSION_ID
import kr.co.jumso.registry.SessionRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
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
    private val chatServerLoad = "chat-server-load"

    // 클라이언트와 연결이 맺어진 후
    override fun afterConnectionEstablished(session: WebSocketSession) {
        super.afterConnectionEstablished(session)

        // session에서 memberId를 가져온다.
        val memberId = getMemberId(session)

        // sessionRegistry에 session을 추가한다.
        sessionRegistry.addSession(session)

        // redis에 memberId를 키로 현재 서버의 포트와 session의 id를 값으로 저장한다.
        // ToDo: TTL은 1일
        redisTemplate.opsForHash<String, String>().put(
            MEMBER_ID_TO_SERVER_PORT_AND_SESSION_ID.key,
            memberId.toString(),
            "${now().format(ofPattern("yyyy-MM-dd"))} $serverPort ${session.id}"
        )

        // redis에 "chat-server-load"로 ws 개수 보고
        redisTemplate.opsForZSet().add(chatServerLoad, serverPort, sessionRegistry.getSessionCount())

        // 채팅방 목록을 조회하여 클라이언트에게 전달
        val result = objectMapper.writeValueAsString(chatRoomService.findChatRoomByMemberId(memberId.toLong()))

        session.sendMessage(TextMessage(result))
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // Message 객체로 변환
        val payload = message.payload

        val responseMessageObject = objectMapper.readValue(payload, RequestMessage::class.java)

        // ToDo: MessageType에 따라 분기처리 예정
        when (responseMessageObject.type) {
            // 채팅방 목록 조회
            REQUEST_SELECT_CHAT_ROOM_LIST -> {
                requestSelectChatRoomList(session)
            }
            // 채팅방 생성
            REQUEST_CREATE_CHAT_ROOM -> {
                requestCreateChatRoom(responseMessageObject, session)
            }
            // 채팅방 삭제
            REQUEST_DELETE_CHAT_ROOM -> {
                // message에서 DeleteChatRoomRequest를 가져온다.
                val deleteChatRoomRequest =
                    objectMapper.convertValue(responseMessageObject.data, DeleteChatRoomRequest::class.java)

                // session에서 memberId를 가져온다.
                val memberId = getMemberId(session)

                // 채팅방 삭제 요청
                chatRoomService.deleteChatRoom(
                    memberId,
                    deleteChatRoomRequest
                )

                val result = ResponseMessage(
                    status = SUCCESS,
                    type = RESPONSE_DELETE_CHAT_ROOM,
                    data = ""
                )

                session.sendMessage(TextMessage(objectMapper.writeValueAsString(result)))
            }
            // 채팅방의 메시지 조회
            REQUEST_SELECT_CHAT_ROOM_MESSAGES -> {

            }
            // 채팅 보내기
            REQUEST_SEND_CHAT -> {

            }
            else -> {
                // ToDo: 예외 처리
            }
        }
    }

    private fun requestSelectChatRoomList(session: WebSocketSession) {
        // session에서 memberId를 가져온다.
        val memberId = getMemberId(session)

        // 채팅방 목록을 조회하여 클라이언트에게 전달
        val chatRoomList = chatRoomService.findChatRoomByMemberId(
                memberId
            )

        val result = ResponseMessage(
            status = SUCCESS,
            type = RESPONSE_SELECT_CHAT_ROOM_LIST,
            data = chatRoomList
        )

        session.sendMessage(TextMessage(objectMapper.writeValueAsString(result)))
    }

    private fun requestCreateChatRoom(
        requestMessageObject: RequestMessage,
        session: WebSocketSession
    ) {
        // message에서 CreateChatRoomRequest를 가져온다.
        val createChatRoomRequest =
            objectMapper.convertValue(requestMessageObject.data, CreateChatRoomRequest::class.java)

        // session에서 memberId를 가져온다.
        val memberId = getMemberId(session)

        // 채팅방 생성 요청
        val newChatRoom = chatRoomService.createChatRoom(
                memberId,
                createChatRoomRequest
            )

        val result = ResponseMessage(
            status = SUCCESS,
            type = RESPONSE_CREATE_CHAT_ROOM,
            data = newChatRoom
        )

        session.sendMessage(TextMessage(objectMapper.writeValueAsString(result)))
    }

    // 통신 에러시
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        super.handleTransportError(session, exception)

        // sessionRegistry에서 session을 제거한다.
        sessionRegistry.removeSession(session)

        // redis에서 memberId를 키로 현재 서버의 포트와 session의 id를 삭제한다.
        redisTemplate.opsForHash<String, String>().delete(MEMBER_ID_TO_SERVER_PORT_AND_SESSION_ID.key, session.attributes["memberId"])

        // redis에 "chat-server-load"로 ws 개수 보고
        redisTemplate.opsForZSet().add(chatServerLoad, serverPort, sessionRegistry.getSessionCount())
    }

    // 연결 종료시
    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        super.afterConnectionClosed(session, closeStatus)

        // sessionRegistry에서 session을 제거한다.
        sessionRegistry.removeSession(session)

        // redis에서 memberId를 키로 현재 서버의 포트와 session의 id를 삭제한다.
        redisTemplate.opsForHash<String, String>().delete(MEMBER_ID_TO_SERVER_PORT_AND_SESSION_ID.key, session.attributes["memberId"])

        // redis에 "chat-server-load"로 ws 개수 보고
        redisTemplate.opsForZSet().add(chatServerLoad, serverPort, sessionRegistry.getSessionCount())
    }

    private fun getMemberId(session: WebSocketSession): Long {
        session.attributes["memberId"]
            ?.let {
                it as String
                return it.toLong()
            }
            ?: run {
                throw IllegalArgumentException("memberId가 없습니다.")
            }
    }
}
