package kr.co.jumso.service

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.jumso.annotation.Valid
import kr.co.jumso.dto.chat.KafkaMessage
import kr.co.jumso.dto.chat.ResponseMessage
import kr.co.jumso.dto.chat.response.*
import kr.co.jumso.enumstorage.chat.MessageStatus.SUCCESS
import kr.co.jumso.enumstorage.chat.MessageType.*
import kr.co.jumso.registry.SessionRegistry
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

@Service
class ChatKafkaConsumer(
    private val sessionRegistry: SessionRegistry,

    private val objectMapper: ObjectMapper,
) {
    // Kafka에서 들어오는 메시지를 처리
    @KafkaListener(topics = ["\${spring.kafka.consumer.topic}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun listen(message: String) {
        // json 파싱
        val chatMessageObject = objectMapper.readValue(message, KafkaMessage::class.java)

        processKafkaMessage(chatMessageObject)
    }

    // 서버가 채팅 메시지를 수신할 때
    private fun processKafkaMessage(@Valid kafkaMessage: KafkaMessage) {
        // sessionRegistry에서 해당 채팅 대상자의 session을 가져온다.
        val targetSession = sessionRegistry.getSession(kafkaMessage.targetMemberId)
            ?: return

        when (kafkaMessage.type) {
            SYSTEM -> {
                // ToDo: 시스템 메시지 처리
            }
            ERROR -> {
                // ToDo: 에러 메시지 처리
            }
            SELECT_CHAT_ROOM_LIST -> {
                val selectChatRoomResponseList =
                    objectMapper.convertValue(kafkaMessage.data, List::class.java) as List<ChatRoomResponse>

                selectChatRoomList(selectChatRoomResponseList, targetSession)
            }
            CREATE_CHAT_ROOM -> {
                val createChatRoomResponse =
                    objectMapper.convertValue(kafkaMessage.data, CreateChatRoomResponse::class.java)

                createChatRoom(createChatRoomResponse, targetSession)
            }
            DELETE_CHAT_ROOM -> {
                val deleteChatRoomResponse =
                    objectMapper.convertValue(kafkaMessage.data, DeleteChatRoomResponse::class.java)

                deleteChatRoom(deleteChatRoomResponse, targetSession)
            }
            SELECT_CHAT_ROOM_MESSAGES -> {
                val selectChatRoomMessagesResponse =
                    objectMapper.convertValue(kafkaMessage.data, SelectChatRoomMessagesResponse::class.java)

                selectChatRoomMessages(selectChatRoomMessagesResponse, targetSession)
            }
            // 채팅 메시지 수신 전송
            CHAT_MESSAGE -> {
                val chatMessageResponse: ChatMessageResponse =
                    objectMapper.convertValue(kafkaMessage.data, ChatMessageResponse::class.java)

                sendChatMessage(chatMessageResponse, targetSession)
            }
            EMAIL -> {}
        }
    }

    private fun selectChatRoomList(@Valid chatRoomList: List<ChatRoomResponse>, session: WebSocketSession) {
        val result = ResponseMessage(
            status = SUCCESS,
            type = SELECT_CHAT_ROOM_LIST,
            data = chatRoomList,
        )

        // 채팅방 목록 조회 응답 전송
        session.sendMessage(TextMessage(objectMapper.writeValueAsString(result)))
    }

    private fun createChatRoom(@Valid createChatRoomResponse: CreateChatRoomResponse, session: WebSocketSession) {
        val result = ResponseMessage(
            status = SUCCESS,
            type = CREATE_CHAT_ROOM,
            data = createChatRoomResponse,
        )

        // 채팅방 생성 응답 전송
        session.sendMessage(TextMessage(objectMapper.writeValueAsString(result)))
    }

    private fun deleteChatRoom(@Valid deleteChatRoomResponse: DeleteChatRoomResponse, session: WebSocketSession) {
        val result = ResponseMessage(
            status = SUCCESS,
            type = DELETE_CHAT_ROOM,
            data = deleteChatRoomResponse,
        )

        // 채팅방 삭제 응답 전송
        session.sendMessage(TextMessage(objectMapper.writeValueAsString(result)))
    }

    private fun selectChatRoomMessages(@Valid selectChatRoomMessagesResponse: SelectChatRoomMessagesResponse, session: WebSocketSession) {
        val result = ResponseMessage(
            status = SUCCESS,
            type = SELECT_CHAT_ROOM_MESSAGES,
            data = selectChatRoomMessagesResponse,
        )

        // 채팅방의 메시지 조회 응답 전송
        session.sendMessage(TextMessage(objectMapper.writeValueAsString(result)))
    }

    private fun sendChatMessage(@Valid chatMessageResponse: ChatMessageResponse, session: WebSocketSession) {
        val result = ResponseMessage(
            status = SUCCESS,
            type = CHAT_MESSAGE,
            data = chatMessageResponse,
        )

        // 채팅 메시지 전송
        session.sendMessage(TextMessage(objectMapper.writeValueAsString(result)))
    }
}
