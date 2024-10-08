package kr.co.jumso.domain.auth.dto

import jakarta.validation.constraints.NotBlank

data class RequestResetPasswordRequest(
    @field:NotBlank
    val email: String,
)
