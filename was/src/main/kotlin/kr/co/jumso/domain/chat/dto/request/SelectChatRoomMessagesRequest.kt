package kr.co.jumso.domain.chat.dto.request

import jakarta.validation.constraints.NotNull

data class SelectChatRoomMessagesRequest(
    @field:NotNull
    val chatRoomId: Long,

    @field:NotNull
    val lastMessageId: Long,
)