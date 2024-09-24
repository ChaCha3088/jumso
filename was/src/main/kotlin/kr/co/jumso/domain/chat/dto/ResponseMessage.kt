package kr.co.jumso.domain.chat.dto

import jakarta.validation.constraints.NotNull
import kr.co.jumso.domain.chat.enumstorage.MessageStatus
import kr.co.jumso.domain.chat.enumstorage.MessageType

data class ResponseMessage(
    @field:NotNull
    val status: MessageStatus,

    @field:NotNull
    val type: MessageType,

    @field:NotNull
    val data: Any,
)
