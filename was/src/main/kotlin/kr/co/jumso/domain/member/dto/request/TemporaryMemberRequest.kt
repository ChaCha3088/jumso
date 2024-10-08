package kr.co.jumso.domain.member.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kr.co.jumso.domain.member.entity.TemporaryMember
import org.springframework.security.crypto.password.PasswordEncoder

data class TemporaryMemberRequest(
    @field:NotBlank(message = "이메일은 필수 입력 값입니다.")
    var username: String,

    @field:NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    var password: String,

    @field:NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    var passwordConfirm: String,

    @field:NotBlank(message = "별명은 필수 입력 값입니다.")
    var nickname: String,

    @field:NotNull(message = "회사 Email ID는 필수 입력 값입니다.")
    val companyEmailId: Long,
)
