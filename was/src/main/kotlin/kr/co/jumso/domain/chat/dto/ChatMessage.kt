package kr.co.jumso.domain.chat.dto

data class ChatMessage(
    val chatId: Long,

    val senderId: Long,
    val senderName: String,

    val roomId: Long,

    val message: String,
)