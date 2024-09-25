package kr.co.jumso.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.littlenb.snowflake.sequence.IdGenerator
import kr.co.jumso.annotation.Valid
import kr.co.jumso.domain.chat.dto.ChatMessageRedis
import kr.co.jumso.domain.chat.dto.request.ChatMessageRequest
import kr.co.jumso.domain.chat.dto.request.ChatMessageResponse
import kr.co.jumso.domain.chat.dto.request.SelectChatRoomMessagesRequest
import kr.co.jumso.enumstorage.RedisKeys.CHAT_MESSAGE_STORAGE
import kr.co.jumso.enumstorage.RedisKeys.CHAT_ROOM_MEMBERS
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.Long.Companion.MAX_VALUE

@Service
@Transactional(readOnly = true)
class ChatService(
    private val redisTemplate: RedisTemplate<String, Any>,

    private val objectMapper: ObjectMapper,

    private val idGenerator: IdGenerator,
) {
    fun findChatMessages(
        memberId: Long,
        @Valid selectChatRoomMessagesRequest: SelectChatRoomMessagesRequest
    ): List<ChatMessageRedis> {
        val chatMessageJson = redisTemplate.opsForZSet().rangeByScore(
            "$CHAT_MESSAGE_STORAGE ${selectChatRoomMessagesRequest.chatRoomId}",
            selectChatRoomMessagesRequest.lastMessageId.toDouble(),
            MAX_VALUE.toDouble(),
        ) as Set<String>?

        val chatMessagesRedis = chatMessageJson?.map {
            objectMapper.readValue(it, ChatMessageRedis::class.java)
        }

        return chatMessagesRedis ?: emptyList()
    }

    @Transactional
    fun saveChatMessage(
        memberId: Long,
        @Valid chatMessageRequest: ChatMessageRequest
    ): ChatMessageResponse {
        // message의 길이를 확인한다.
        if (chatMessageRequest.message.length > 1000) {
            throw IllegalArgumentException("메시지는 1000자 이하로 입력해주세요.")
        }

        // redis에서 채팅방에 속한 유저들을 가져온다.
        redisTemplate.opsForSet().members("$CHAT_ROOM_MEMBERS ${chatMessageRequest.targetChatRoomId}")
            ?.map { it.toString().toLong() }
                ?.let { chatRoomMembers ->
                    // 채팅방에 속한 유저가 아니면
                    if (!chatRoomMembers.contains(memberId)) {
                        throw IllegalArgumentException("채팅방에 속한 유저가 아닙니다.")
                    }
                }
            ?: throw IllegalArgumentException("채팅방이 존재하지 않습니다.")

        val newChatMessageRedis = ChatMessageRedis(
            idGenerator.nextId(),
            memberId,
            chatMessageRequest.message,
        )

        redisTemplate.opsForZSet().add(
            "$CHAT_MESSAGE_STORAGE ${chatMessageRequest.targetChatRoomId}",
            objectMapper.writeValueAsString(newChatMessageRedis),
            newChatMessageRedis.chatId.toDouble(),
        )

        return ChatMessageResponse(
            chatId = newChatMessageRedis.chatId,
            senderId = newChatMessageRedis.senderId,
            chatRoomId = chatMessageRequest.targetChatRoomId,
            message = newChatMessageRedis.message,
        )
    }
}