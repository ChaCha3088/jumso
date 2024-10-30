package kr.co.jumso.dto.chat.response

data class ChatRoomResponse(
    val chatRoomId: Long,
    val memberIds: Set<Long>,
    val title: String,
)
