package kr.co.jumso.domain.chat.dto

import kr.co.jumso.domain.chat.enumstorage.MessageType

data class Message(
    val type: MessageType,
    val data: Any,
)
