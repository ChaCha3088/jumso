package kr.co.jumso.dto.chat.response

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class ChatMessageResponse(
    @field:NotNull
    val chatId: Long,

    @field:NotNull
    val chatRoomId: Long,

    @field:NotNull
    val senderId: Long,

    @field:NotBlank
    val message: String,
)
