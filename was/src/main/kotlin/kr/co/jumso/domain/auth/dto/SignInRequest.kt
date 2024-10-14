package kr.co.jumso.domain.auth.dto

import jakarta.validation.constraints.NotBlank

data class SignInRequest(
    @field:NotBlank(message = "이메일은 필수 입력 값입니다.")
    var email: String,

    @field:NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    var password: String
)