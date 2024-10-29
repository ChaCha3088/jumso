package kr.co.jumso.dto.chat.response

import jakarta.validation.constraints.NotNull

data class DeleteChatRoomResponse(
    @field:NotNull
    val chatRoomId: Long
)
