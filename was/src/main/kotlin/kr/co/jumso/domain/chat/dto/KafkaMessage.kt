package kr.co.jumso.domain.chat.dto

import jakarta.validation.constraints.NotNull
import kr.co.jumso.domain.chat.enumstorage.MessageType

data class KafkaMessage(
    @field:NotNull
    val type: MessageType,

    @field:NotNull
    val targetMemberId: Long,

    @field:NotNull
    val data: Any,
)