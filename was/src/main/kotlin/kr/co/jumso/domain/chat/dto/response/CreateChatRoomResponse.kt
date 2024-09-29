package kr.co.jumso.domain.chat.dto.response

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class CreateChatRoomResponse(
    @field:NotNull
    val chatRoomId: Long,

    @field:NotBlank(message = "채팅방 제목은 필수입니다.")
    val title: String,

    @field:NotEmpty(message = "대화 상대는 필수입니다.")
    val memberIds: Set<Long>
)
