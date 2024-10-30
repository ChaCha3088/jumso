package kr.co.jumso.dto.chat.response

import jakarta.validation.constraints.NotNull

data class SelectChatRoomMessagesResponse(
    @field:NotNull
    val messages: List<ChatMessageResponse>
)
