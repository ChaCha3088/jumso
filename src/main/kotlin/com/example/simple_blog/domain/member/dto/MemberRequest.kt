package com.example.simple_blog.domain.member.dto

import com.example.simple_blog.domain.member.entity.Member
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.security.crypto.password.PasswordEncoder

data class MemberRequest(
    @field:NotBlank(message = "이메일은 필수 입력 값입니다.")
    val email: String?,

    @field:NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    var password: String?,

    @field:NotBlank(message = "이름은 필수 입력 값입니다.")
    val name: String?,

    @field:NotBlank(message = "별명은 필수 입력 값입니다.")
    val nickname: String?,

    @field:NotNull(message = "회사 ID는 필수 입력 값입니다.")
    val companyId: Long?,
) {
    fun toEntity(passwordEncoder: PasswordEncoder) = Member(
        email = email!!,
        password = passwordEncoder.encode(password),
        name = name!!,
        nickname = nickname!!,
        companyId = companyId!!,
    )
}
