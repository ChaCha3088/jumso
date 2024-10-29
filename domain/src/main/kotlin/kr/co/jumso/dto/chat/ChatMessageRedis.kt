package kr.co.jumso.dto.chat

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ChatMessageRedis(
    @field:NotNull
    val senderId: Long,

    @field:NotBlank
    val message: String,
)
