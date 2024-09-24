package kr.co.jumso.domain.chat.dto

import jakarta.validation.constraints.NotNull

data class DeleteChatRoomRequest(
    @field:NotNull
    val chatRoomId: Long
)
