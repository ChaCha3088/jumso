package kr.co.jumso.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.littlenb.snowflake.sequence.IdGenerator
import kr.co.jumso.annotation.Valid
import kr.co.jumso.dto.chat.ChatMessageRedis
import kr.co.jumso.dto.chat.KafkaMessage
import kr.co.jumso.dto.chat.redis.DefaultTypedTuple
import kr.co.jumso.dto.chat.request.ChatMessageRequest
import kr.co.jumso.dto.chat.request.SelectChatRoomMessagesRequest
import kr.co.jumso.dto.chat.response.ChatMessageResponse
import kr.co.jumso.enumstorage.chat.MessageType.CHAT_MESSAGE
import kr.co.jumso.enumstorage.chat.RedisKeys.*
import kr.co.jumso.enumstorage.KafkaConfig.KAFKA_CHAT_SERVER
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.Long.Companion.MAX_VALUE

@Service
@Transactional(readOnly = true)
class ChatService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val kafkaTemplate: KafkaTemplate<String, Any>,

    private val objectMapper: ObjectMapper,

    private val idGenerator: IdGenerator,
) {
    fun findChatMessages(
        memberId: Long,
        @Valid selectChatRoomMessagesRequest: SelectChatRoomMessagesRequest
    ): List<ChatMessageResponse> {
        // 해당 member가 채팅방에 속해있는지 확인한다.
        redisTemplate.opsForSet().members("$CHAT_ROOM_MEMBERS ${selectChatRoomMessagesRequest.chatRoomId}")
            ?.map { it.toString().toLong() }
            ?.let { chatRoomMembers ->
                if (!chatRoomMembers.contains(memberId)) {
                    throw IllegalArgumentException("채팅방에 속한 유저가 아닙니다.")
                }
            }
            ?: throw IllegalArgumentException("채팅방이 존재하지 않습니다.")

        val chatMessageJson = redisTemplate.opsForZSet().rangeByScoreWithScores(
            "$CHAT_MESSAGE_STORAGE ${selectChatRoomMessagesRequest.chatRoomId}",
            selectChatRoomMessagesRequest.lastMessageId.toDouble(),
            MAX_VALUE.toDouble(),
        )

        val chatMessages = chatMessageJson?.map {
            // DefaultTypedTuple [score=6.033584712318976E15, value={"senderId":0,"message":"채팅방이 생성되었습니다."}]
            val defaultTypedTuple = objectMapper.convertValue(it, DefaultTypedTuple::class.java)

            val chatMessageRedis = objectMapper.readValue(defaultTypedTuple.value, ChatMessageRedis::class.java)

            ChatMessageResponse(
                chatId = defaultTypedTuple.score.toLong(),
                chatRoomId = selectChatRoomMessagesRequest.chatRoomId,
                senderId = chatMessageRedis.senderId,
                message = chatMessageRedis.message,
            )
        }

        return chatMessages ?: emptyList()
    }

    @Transactional
    fun saveChatMessage(
        memberId: Long,
        @Valid chatMessageRequest: ChatMessageRequest
    ) {
        // message의 길이를 확인한다.
        if (chatMessageRequest.message.length > 1000) {
            throw IllegalArgumentException("메시지는 1000자 이하로 입력해주세요.")
        }

        val newChatId = idGenerator.nextId()

        val newChatMessageRedis = ChatMessageRedis(
            memberId,
            chatMessageRequest.message,
        )

        // redis에서 채팅방에 속한 유저들을 가져온다.
        redisTemplate.opsForSet().members("$CHAT_ROOM_MEMBERS ${chatMessageRequest.targetChatRoomId}")
            ?.map { it.toString().toLong() }
                ?.let { chatRoomMembers ->
                    // 채팅방에 속한 유저가 아니면
                    if (!chatRoomMembers.contains(memberId)) {
                        throw IllegalArgumentException("채팅방에 속한 유저가 아닙니다.")
                    }

                    // redis에 각 회원들이 접속하고 있는지 확인한다.
                    chatRoomMembers.forEach { chatRoomMemberId ->
                        val membersServerPort = redisTemplate.opsForHash<String, String>()
                            .get(MEMBER_ID_TO_SERVER_PORT.toString(), chatRoomMemberId.toString())

                        val newMessage = ChatMessageResponse(
                            chatId = newChatId,
                            chatRoomId = chatRoomMemberId,
                            senderId = memberId,
                            message = chatMessageRequest.message,
                        )

                        membersServerPort
                            // membersServerPort가 있으면
                            ?.let {
                                // yyyy-MM-dd serverPort 포맷
                                val serverPort = it.split(" ")[1]

                                // kafka로 메시지를 전송한다.
                                kafkaTemplate.send(
                                    "$KAFKA_CHAT_SERVER-$serverPort",
                                    objectMapper.writeValueAsString(
                                        KafkaMessage(
                                            type = CHAT_MESSAGE,
                                            targetMemberId = chatRoomMemberId,
                                            data = newMessage
                                        )
                                    )
                                )
                            }
                            // membersServerPort가 없으면
                            ?: {
                                // ToDo: 푸시 알림 요청
                                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 푸시 알림 요청 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                            }
                    }
                }
            ?: throw IllegalArgumentException("채팅방이 존재하지 않습니다.")

        // redis에 채팅 메시지를 저장한다.
        redisTemplate.opsForZSet().add(
            "$CHAT_MESSAGE_STORAGE ${chatMessageRequest.targetChatRoomId}",
            objectMapper.writeValueAsString(newChatMessageRedis),
            newChatId.toDouble(),
        )
    }
}
