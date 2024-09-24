package kr.co.jumso.domain.chat.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ChatMessage(
    @field:NotNull
    val chatId: Long,

    @field:NotNull
    val senderId: Long,
    @field:NotBlank
    val senderName: String,

    @field:NotNull
    val roomId: Long,

    @field:NotBlank
    val message: String,
)