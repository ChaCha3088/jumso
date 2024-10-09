package kr.co.jumso.domain.member.dto.request

import jakarta.validation.constraints.NotBlank

data class UpdateIntroductionRequest(
    @field:NotBlank(message = "소개는 필수 입력 값입니다.")
    val introduction: String?
)
