package kr.co.jumso.dto

import kr.co.jumso.enumstorage.MessageType

data class MessageResponse(
    val type: MessageType,
    val data: Any,
)
