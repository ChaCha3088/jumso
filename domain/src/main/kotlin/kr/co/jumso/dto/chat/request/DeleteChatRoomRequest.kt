package kr.co.jumso.dto.chat.request

import jakarta.validation.constraints.NotNull

data class DeleteChatRoomRequest(
    @field:NotNull
    val chatRoomId: Long
)
