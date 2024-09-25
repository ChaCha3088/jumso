package kr.co.jumso.domain.chat.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ChatMessageResponse(
    @field:NotNull
    val chatId: Long,

    @field:NotNull
    val senderId: Long,

    @field:NotNull
    val chatRoomId: Long,

    @field:NotBlank
    val message: String,
)
