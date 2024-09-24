package kr.co.jumso.chat.service

import kr.co.jumso.domain.chat.dto.ChatRoomResponse
import kr.co.jumso.domain.chat.dto.CreateChatRoomRequest
import kr.co.jumso.domain.chat.entity.ChatRoom
import kr.co.jumso.domain.chat.repository.ChatRoomRepository
import kr.co.jumso.domain.chat.repository.MemberChatRoomRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.littlenb.snowflake.sequence.IdGenerator
import jakarta.validation.Validator
import kr.co.jumso.domain.chat.dto.ChatMessage
import kr.co.jumso.domain.chat.dto.Message
import kr.co.jumso.domain.chat.enumstorage.MessageType.RESPONSE_SELECT_CHAT_ROOM_LIST
import kr.co.jumso.domain.chat.enumstorage.MessageType.SYSTEM
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val memberChatRoomRepository: MemberChatRoomRepository,

    // Redis의 Sorted Set을 사용하여 채팅 서버 부하 정보를 저장
    private val redisTemplate: RedisTemplate<String, Any>,

    private val idGenerator: IdGenerator,
    private val objectMapper: ObjectMapper,
) {
    private val redisChatServerLoad = "chat-server-load"
    private val redisChatMessage = "chat-message-"
    private val redisMemberChatServer = "member-chat-server"
    private val redisMemberChatSet = "member-chat-set"
    private val systemName = "system"

    fun findChatRoomByMemberId(memberId: Long): Message {
        val chatRoomResponses = chatRoomRepository.findChatRoomsByMemberId(memberId)
            .map { chatRoom ->
                ChatRoomResponse(
                    chatRoom.id!!,
                    chatRoom.title,
                )
            }

        return Message(
            RESPONSE_SELECT_CHAT_ROOM_LIST,
            chatRoomResponses
        )
    }

    @Transactional
    fun createChatRoom(memberId: Long, createChatRoomRequest: CreateChatRoomRequest): ChatRoomResponse {
        var chatRoom: ChatRoom? = null

        when (createChatRoomRequest.targets.size) {
            0 -> {
                throw IllegalArgumentException("채팅 상대가 존재하지 않습니다.")
            }
            1 -> {
                // 1:1 채팅
                // 상대와 채팅방이 존재하는지 확인
                memberChatRoomRepository.findExistingMemberChatRoom(
                    memberId,
                    createChatRoomRequest.targets.first()
                )
                    // 존재하면
                    ?.let {
                        chatRoom = it.chatRoom
                    }
                    // 존재하지 않으면
                    ?: run {
                        // 채팅방 생성
                        chatRoom = createChatRoom(createChatRoomRequest, memberId)
                    }
            }
            in 2..99 -> {
                // 그룹 채팅
                // 상대들과 채팅방이 존재하는지 확인하지 않고 채팅방 생성
                chatRoom = createChatRoom(createChatRoomRequest, memberId)
            }
            else -> {
                throw IllegalArgumentException("채팅 상대는 최대 99명까지 가능합니다.")
            }
        }

        val newMembersId = chatRoom!!.memberChatRooms.map {
            it.memberId
        }.toMutableSet()

        // redis에 채팅방 참여 회원 정보 저장
        newMembersId.map {
            redisTemplate.opsForSet().add("${redisMemberChatSet}-${chatRoom!!.id}", it.toString())
        }

        // redis에 채팅방 생성 요청
        // 채팅방에 "채팅방이 생성되었습니다." 메시지 추가
        val systemMessageId = idGenerator.nextId()
        val systemMessage = Message(
            type = SYSTEM,
            data = ChatMessage(
                chatId = systemMessageId,

                senderId = memberId,
                senderName = systemName,

                roomId = chatRoom!!.id!!,

                message = "채팅방이 생성되었습니다."
            )
        )

        // Json으로 변환
        val jsonChatMessage = objectMapper.writeValueAsString(systemMessage)

        // 채팅방에 메시지 추가
        redisTemplate.opsForZSet().add(redisChatMessage + chatRoom!!.id, jsonChatMessage, systemMessageId.toDouble())

        // redis에 회원이 속한 채팅 서버를 조회하고
        redisTemplate.opsForHash<String, String>().get(redisMemberChatServer, memberId.toString())
            ?.let {
                // ToDo: 해당 Kafka에 메시지 발행
            }
            ?: run {
                // ToDo: 아니면 푸시 알림 요청
            }

        return ChatRoomResponse(
            chatRoomId = chatRoom!!.id!!,
            title = chatRoom!!.title,
        )
    }

    private fun createChatRoom(
        createChatRoomRequest: CreateChatRoomRequest,
        memberId: Long,
    ): ChatRoom {
        val targetMemberIds = createChatRoomRequest.targets.toMutableSet()
        targetMemberIds.add(memberId)

        val chatRoom = ChatRoom(
            title = createChatRoomRequest.title,
            newMemberIds = targetMemberIds
        )

        return chatRoomRepository.save(chatRoom)
    }
}