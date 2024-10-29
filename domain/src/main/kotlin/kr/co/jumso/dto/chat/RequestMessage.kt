package kr.co.jumso.dto.chat

import jakarta.validation.constraints.NotNull
import kr.co.jumso.enumstorage.chat.MessageType

data class RequestMessage(
    @field:NotNull
    val type: MessageType,

    @field:NotNull
    val data: Any,
)
