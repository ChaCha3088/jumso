package com.example.simple_blog.domain.chat.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class CreateChatRoomRequest(
    @field:NotBlank(message = "채팅방 제목은 필수입니다.")
    val title: String,

    @field:NotEmpty(message = "대화 상대는 필수입니다.")
    val targets: Set<Long>
)