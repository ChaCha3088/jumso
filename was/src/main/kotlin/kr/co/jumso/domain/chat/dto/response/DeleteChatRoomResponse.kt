package kr.co.jumso.domain.chat.dto.response

import jakarta.validation.constraints.NotNull

data class DeleteChatRoomResponse(
    @field:NotNull
    val chatRoomId: Long
)
