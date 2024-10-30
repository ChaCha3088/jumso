package kr.co.jumso.dto.auth

import jakarta.validation.constraints.NotBlank

data class RequestResetPasswordRequest(
    @field:NotBlank
    val email: String,
)
