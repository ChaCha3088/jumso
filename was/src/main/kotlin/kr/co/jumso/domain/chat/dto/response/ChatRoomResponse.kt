package kr.co.jumso.domain.chat.dto.response

data class ChatRoomResponse(
    val chatRoomId: Long,
    val memberIds: Set<Long>,
    val title: String,
)
