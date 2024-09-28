package kr.co.jumso.handler

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.jumso.domain.chat.dto.RequestMessage
import kr.co.jumso.domain.chat.dto.ResponseMessage
import kr.co.jumso.domain.chat.dto.request.ChatMessageRequest
import kr.co.jumso.domain.chat.dto.request.CreateChatRoomRequest
import kr.co.jumso.domain.chat.dto.request.DeleteChatRoomRequest
import kr.co.jumso.domain.chat.dto.request.SelectChatRoomMessagesRequest
import kr.co.jumso.domain.chat.enumstorage.MessageStatus
import kr.co.jumso.domain.chat.enumstorage.MessageStatus.SUCCESS
import kr.co.jumso.domain.chat.enumstorage.MessageType.*
import kr.co.jumso.enumstorage.RedisKeys.MEMBER_ID_TO_SERVER_PORT
import kr.co.jumso.registry.SessionRegistry
import kr.co.jumso.service.ChatRoomService
import kr.co.jumso.service.ChatService
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
    private val chatService: ChatService,
    private val chatRoomService: ChatRoomService,

    private val sessionRegistry: SessionRegistry,

    private val redisTemplate: RedisTemplate<String, Any>,

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
        sessionRegistry.addSession(memberId, session)

        // redis에 memberId를 키로 현재 서버의 포트와 session의 id를 값으로 저장한다.
        // ToDo: TTL은 1일
        redisTemplate.opsForHash<String, String>().put(
            MEMBER_ID_TO_SERVER_PORT.toString(),
            memberId.toString(),
            "${now().format(ofPattern("yyyy-MM-dd"))} $serverPort"
        )

        // redis에 "chat-server-load"로 ws 개수 보고
        redisTemplate.opsForZSet().add(chatServerLoad, serverPort, sessionRegistry.getSessionCount())

        // 채팅방 목록을 조회하여 클라이언트에게 전달
        val result = objectMapper.writeValueAsString(chatRoomService.findChatRoomByMemberId(memberId.toLong()))

        session.sendMessage(TextMessage(result))
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
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
                    requestDeleteChatRoom(responseMessageObject, session)
                }
                // 채팅방의 메시지 조회
                REQUEST_SELECT_CHAT_ROOM_MESSAGES -> {
                    requestSelectChatMessages(responseMessageObject, session)
                }
                // 채팅 보내기
                REQUEST_SEND_CHAT_MESSAGE -> {
                    requestSendChat(responseMessageObject, session)
                }
                else -> {
                    throw IllegalArgumentException("알 수 없는 요청입니다.")
                }
            }
        }
        catch (e: Exception) {
            if (session.isOpen) {
                val result = ResponseMessage(
                    status = MessageStatus.ERROR,
                    type =  ERROR,
                    data = e.message ?: "알 수 없는 에러가 발생했습니다."
                )

                session.sendMessage(TextMessage(objectMapper.writeValueAsString(result)))
            }
        }
    }

    private fun requestSendChat(
        requestMessageObject: RequestMessage,
        session: WebSocketSession
    ) {
        // message에서 ChatMessage를 가져온다.
        val chatMessageRequest =
            objectMapper.convertValue(requestMessageObject.data, ChatMessageRequest::class.java)

        // session에서 memberId를 가져온다.
        val memberId = getMemberId(session)

        // 채팅 메시지 저장
        val newChatMessage = chatService.saveChatMessage(
            memberId,
            chatMessageRequest
        )

        val result = ResponseMessage(
            status = SUCCESS,
            type = RESPONSE_SEND_CHAT,
            data = newChatMessage
        )

        session.sendMessage(TextMessage(objectMapper.writeValueAsString(result)))
    }

    private fun requestSelectChatMessages(
        requestMessageObject: RequestMessage,
        session: WebSocketSession
    ) {
        // message에서 SelectChatRoomMessagesRequest를 가져온다.
        val selectChatRoomMessagesRequest =
            objectMapper.convertValue(requestMessageObject.data, SelectChatRoomMessagesRequest::class.java)

        // session에서 memberId를 가져온다.
        val memberId = getMemberId(session)

        // 채팅방의 메시지 조회
        val chatMessages = chatService.findChatMessages(
            memberId,
            selectChatRoomMessagesRequest
        )

        val result = ResponseMessage(
            status = SUCCESS,
            type = RESPONSE_SELECT_CHAT_ROOM_MESSAGES,
            data = chatMessages
        )

        session.sendMessage(TextMessage(objectMapper.writeValueAsString(result)))
    }

    private fun requestDeleteChatRoom(
        requestMessageObject: RequestMessage,
        session: WebSocketSession
    ) {
        // message에서 DeleteChatRoomRequest를 가져온다.
        val deleteChatRoomRequest =
            objectMapper.convertValue(requestMessageObject.data, DeleteChatRoomRequest::class.java)

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

        // 채팅방 목록 조회
        requestSelectChatRoomList(session)
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

        val memberId = getMemberId(session)

        // sessionRegistry에서 session을 제거한다.
        sessionRegistry.removeSession(memberId)

        // redis에서 memberId를 키로 현재 서버의 포트와 session의 id를 삭제한다.
        redisTemplate.opsForHash<String, String>().delete(MEMBER_ID_TO_SERVER_PORT.toString(), session.attributes["memberId"])

        // redis에 "chat-server-load"로 ws 개수 보고
        redisTemplate.opsForZSet().add(chatServerLoad, serverPort, sessionRegistry.getSessionCount())
    }

    // 연결 종료시
    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        super.afterConnectionClosed(session, closeStatus)

        val memberId = getMemberId(session)

        // sessionRegistry에서 session을 제거한다.
        sessionRegistry.removeSession(memberId)

        // redis에서 memberId를 키로 현재 서버의 포트와 session의 id를 삭제한다.
        redisTemplate.opsForHash<String, String>().delete(MEMBER_ID_TO_SERVER_PORT.toString(), session.attributes["memberId"])

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
