package kr.co.jumso.domain.chat.service

import kr.co.jumso.domain.chat.dto.ChatRoomResponse
import kr.co.jumso.domain.chat.dto.Message
import kr.co.jumso.domain.chat.enumstorage.MessageType.RESPONSE_SELECT_CHAT_ROOM_LIST
import kr.co.jumso.domain.chat.repository.ChatRoomRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatRoomService(
    val chatRoomRepository: ChatRoomRepository,
    ) {
    @Transactional
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
}