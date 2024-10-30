package kr.co.jumso.dto.chat.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ChatMessageRequest(
    @field:NotNull
    val targetChatRoomId: Long,

    @field:NotBlank
    val message: String,
)
