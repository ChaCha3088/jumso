package com.example.jumso.domain.chat.service

import com.example.jumso.domain.chat.dto.ChatRoomResponse
import com.example.jumso.domain.chat.dto.CreateChatRoomRequest
import com.example.jumso.domain.chat.dto.Message
import com.example.jumso.domain.chat.entity.ChatRoom
import com.example.jumso.domain.chat.repository.ChatRoomRepository
import com.example.jumso.domain.chat.repository.MemberChatRoomRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.littlenb.snowflake.sequence.IdGenerator
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.Double.Companion.POSITIVE_INFINITY

@Service
@Transactional(readOnly = true)
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val memberChatRoomRepository: MemberChatRoomRepository,
    private val idGenerator: IdGenerator,
    private val objectMapper: ObjectMapper,

    // Redis의 Sorted Set을 사용하여 채팅 서버 부하 정보를 저장
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    private val redisChatServerLoad = "chat-server-load"
    private val redisChatMessage = "chat-message-"
    private val redisMemberChatServer = "member-chat-server"

    @Transactional
    fun createChatRoom(memberId: Long, createChatRoomRequest: CreateChatRoomRequest): ChatRoomResponse {
        var chatRoomId: Long = 0L;
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
                        chatRoomId = it.chatRoom.id!!
                    }
                    // 존재하지 않으면
                    ?: run {
                        // 채팅방 생성
                        chatRoomId = createChatRoom(createChatRoomRequest, memberId)
                    }
            }
            in 2..99 -> {
                // 그룹 채팅
                // 상대들과 채팅방이 존재하는지 확인하지 않고 채팅방 생성
                chatRoomId = createChatRoom(createChatRoomRequest, memberId)
            }
            else -> {
                throw IllegalArgumentException("채팅 상대는 최대 99명까지 가능합니다.")
            }
        }

        // redis에 채팅방 생성 요청
        // 채팅방에 "채팅방이 생성되었습니다." 메시지 추가
        val systemMessage = Message(0L, "채팅방이 생성되었습니다.")

        // Json으로 변환
        val chatMessage = objectMapper.writeValueAsString(systemMessage)

        // 채팅방에 메시지 추가
        redisTemplate.opsForZSet().add(redisChatMessage + chatRoomId, chatMessage, idGenerator.nextId().toDouble())

        // redis에 채팅 서버 부하 정보 요청
        // 채팅 서버 WebSocket 연결 개수를 기반으로 채팅 서버 선택
        // score는 최소값 1개만 score와 함께 조회
        val result = redisTemplate.opsForZSet().rangeByScoreWithScores(redisChatServerLoad, 0.0, POSITIVE_INFINITY, 0, 1)!!.first()

        // 채팅 서버 부하 정보 업데이트
        redisTemplate.opsForZSet().incrementScore(redisChatServerLoad, result.value!!, 1.0)

        // redis에 이 member가 어떤 채팅 서버에 연결되어 있는지 저장
        redisTemplate.opsForHash<Long, Int>().put(redisMemberChatServer, memberId, result.value.toString().toInt())

        return ChatRoomResponse(
            chatRoomId = chatRoomId,
            chatServerPort = result.value.toString().toInt()
        )
    }

    private fun createChatRoom(
        createChatRoomRequest: CreateChatRoomRequest,
        memberId: Long,
    ): Long {
        val targetMemberIds = createChatRoomRequest.targets.toMutableSet()
        targetMemberIds.add(memberId)

        val chatRoom = ChatRoom(
            title = createChatRoomRequest.title,
            newMemberIds = targetMemberIds
        )

        val newChatRoom = chatRoomRepository.save(chatRoom)
        return newChatRoom.id!!
    }
}
