package kr.co.jumso.dto.chat.request

import jakarta.validation.constraints.NotNull

data class SelectChatRoomMessagesRequest(
    @field:NotNull
    val chatRoomId: Long,

    @field:NotNull
    val lastMessageId: Long,
)
