package kr.co.jumso.dto.auth

import jakarta.validation.constraints.NotBlank

data class ResetPasswordRequest(
    @field:NotBlank
    val verificationCode: String,

    @field:NotBlank
    val newPassword: String,

    @field:NotBlank
    val newPasswordConfirm: String
)
