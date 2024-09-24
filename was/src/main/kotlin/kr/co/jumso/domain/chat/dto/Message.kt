package kr.co.jumso.domain.chat.dto

import jakarta.validation.constraints.NotNull
import kr.co.jumso.domain.chat.enumstorage.MessageType

data class Message(
    @field:NotNull
    val type: MessageType,

    @field:NotNull
    val data: Any,
)
