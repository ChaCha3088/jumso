package kr.co.jumso.service

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.jumso.annotation.Valid
import kr.co.jumso.domain.chat.dto.ChatMessage
import kr.co.jumso.domain.chat.dto.request.SelectChatRoomMessagesRequest
import kr.co.jumso.enumstorage.RedisKeys.CHAT_MESSAGE_STORAGE
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.Long.Companion.MAX_VALUE

@Service
@Transactional(readOnly = true)
class ChatService(
    private val redisTemplate: RedisTemplate<String, Any>,

    private val objectMapper: ObjectMapper,
) {
    fun findChatMessages(memberId: Long, @Valid selectChatRoomMessagesRequest: SelectChatRoomMessagesRequest): List<ChatMessage> {
        val chatMessageJson = redisTemplate.opsForZSet().rangeByScore(
            "$CHAT_MESSAGE_STORAGE ${selectChatRoomMessagesRequest.chatRoomId}",
            selectChatRoomMessagesRequest.lastMessageId.toDouble(),
            MAX_VALUE.toDouble(),
        ) as Set<String>?

        val chatMessages = chatMessageJson?.map {
            objectMapper.readValue(it, ChatMessage::class.java)
        }

        return chatMessages ?: emptyList()
    }
}