package kr.co.jumso.dto.chat

import jakarta.validation.constraints.NotNull
import kr.co.jumso.enumstorage.chat.MessageStatus
import kr.co.jumso.enumstorage.chat.MessageType

data class ResponseMessage(
    @field:NotNull
    val status: MessageStatus,

    @field:NotNull
    val type: MessageType,

    @field:NotNull
    val data: Any,
)
