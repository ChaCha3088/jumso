package kr.co.jumso.domain.auth.dto

import jakarta.validation.constraints.NotBlank

data class ResetPasswordRequest(
    @field:NotBlank
    val verificationCode: String,

    @field:NotBlank
    val newPassword: String,

    @field:NotBlank
    val newPasswordConfirm: String
)
