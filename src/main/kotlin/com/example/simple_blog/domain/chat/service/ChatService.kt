package com.example.simple_blog.domain.chat.service

import com.example.simple_blog.domain.chat.dto.CreateChatRoomRequest
import com.example.simple_blog.domain.chat.entity.ChatRoom
import com.example.simple_blog.domain.chat.repository.ChatRoomRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
) {
    @Transactional
    fun createChatRoom(memberId: Long, createChatRoomRequest: CreateChatRoomRequest): ChatRoomResponse {
        when (createChatRoomRequest.targets.size) {
            0 -> {
                throw IllegalArgumentException("채팅 상대가 존재하지 않습니다.")
            }
            1 -> {
                // 1:1 채팅
                // 상대와 채팅방이 존재하는지 확인
                chatRoomRepository
            }
            in 2..99 -> {
                // 그룹 채팅
                // 상대들과 채팅방이 존재하는지 확인하지 않고 채팅방 생성
            }
            else -> {
                throw IllegalArgumentException("채팅 상대는 최대 99명까지 가능합니다.")
            }
        }

        val targetMemberIds = createChatRoomRequest.targets.toMutableSet()
        targetMemberIds.add(memberId)

        val newChatRoom = ChatRoom(
            title = createChatRoomRequest.title,
            newMemberIds = targetMemberIds
        )

        return chatRoomRepository.save(newChatRoom)
    }
}