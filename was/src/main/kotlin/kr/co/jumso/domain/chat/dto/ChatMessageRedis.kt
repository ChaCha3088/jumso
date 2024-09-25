package kr.co.jumso.domain.chat.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ChatMessageRedis(
    @field:NotNull
    val chatId: Long,

    @field:NotNull
    val senderId: Long,

    @field:NotBlank
    val message: String,
)
