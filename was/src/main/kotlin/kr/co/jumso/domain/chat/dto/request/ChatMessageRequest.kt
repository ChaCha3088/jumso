package kr.co.jumso.domain.chat.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ChatMessageRequest(
    @field:NotNull
    val targetChatRoomId: Long,

    @field:NotBlank
    val message: String,
)
