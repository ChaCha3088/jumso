package kr.co.jumso.domain.chat.dto.response

import jakarta.validation.constraints.NotNull

data class SelectChatRoomMessagesResponse(
    @field:NotNull
    val messages: List<ChatMessageResponse>
)
