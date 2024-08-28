package com.example.simple_blog.domain.member.dto

import com.example.simple_blog.domain.member.entity.TemporaryMember
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.security.crypto.password.PasswordEncoder

data class TemporaryMemberRequest(
    @field:NotBlank(message = "이메일은 필수 입력 값입니다.")
    val username: String?,

    @field:NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    var password: String?,

    @field:NotBlank(message = "별명은 필수 입력 값입니다.")
    val nickname: String?,

    @field:NotNull(message = "회사 Email ID는 필수 입력 값입니다.")
    val companyEmailId: Long?,
) {
    fun toEntity(passwordEncoder: PasswordEncoder) = TemporaryMember(
        username = username!!,
        password = passwordEncoder.encode(password),
        nickname = nickname!!,
        companyEmailId = companyEmailId!!,
    )
}