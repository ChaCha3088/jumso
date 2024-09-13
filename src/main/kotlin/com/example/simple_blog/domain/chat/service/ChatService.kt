package com.example.simple_blog.domain.chat.service

import com.example.simple_blog.domain.chat.dto.Message
import com.example.simple_blog.domain.chat.dto.ChatRoomResponse
import com.example.simple_blog.domain.chat.dto.CreateChatRoomRequest
import com.example.simple_blog.domain.chat.entity.ChatRoom
import com.example.simple_blog.domain.chat.repository.ChatRoomRepository
import com.example.simple_blog.domain.chat.repository.MemberChatRoomRepository
import com.littlenb.snowflake.sequence.IdGenerator
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val memberChatRoomRepository: MemberChatRoomRepository,
    private val idGenerator: IdGenerator,

    // Redis의 Sorted Set을 사용하여 채팅 서버 부하 정보를 저장
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    private val chatServerLoad = "chat-server-load"
    private val chatMessage = "chat-message"

    @Transactional
    fun createChatRoom(memberId: Long, createChatRoomRequest: CreateChatRoomRequest): ChatRoomResponse {
        var chatRoomId = 0L;
        when (createChatRoomRequest.targets.size) {
            0 -> {
                throw IllegalArgumentException("채팅 상대가 존재하지 않습니다.")
            }
            1 -> {
                // 1:1 채팅
                // 상대와 채팅방이 존재하는지 확인
                memberChatRoomRepository.findExistingMemberChatRoom(memberId, createChatRoomRequest.targets.first())
                    // 존재하면
                    ?.let {
                        chatRoomId = it.chatRoom.id!!
                    }
                    // 존재하지 않으면
                    ?: {
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

        // ToDo : json 형태로 변환하여 저장
        val chatMessage = systemMessage.toJson()

        redisTemplate.opsForZSet().add(chatMessage + chatRoomId, "채팅방이 생성되었습니다.", idGenerator.nextId().toDouble())

        // redis에 채팅 서버 부하 정보 요청
        // 채팅 서버 WebSocket 연결 개수를 기반으로 채팅 서버 선택
        val result = redisTemplate.opsForZSet().range(chatServerLoad, 0, 0)

        // result가 없으면 채팅 서버 id 0번, 있으면 result[0]번 채팅 서버 선택
        return ChatRoomResponse(
            chatRoomId = chatRoomId,
            chatServerId = result!!.firstOrNull()?.toString()?.toInt() ?: 0
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
