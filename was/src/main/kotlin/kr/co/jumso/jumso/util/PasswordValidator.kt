package kr.co.jumso.util

import com.example.jumso.domain.member.exception.PasswordInvalidException
import org.springframework.stereotype.Component

@Component
class PasswordValidator {
    fun validate(username: String, password: String): Boolean {
        // 길이는 8자 이상 and 255자 이하
        if (password.length < 8 || password.length > 255) {
            throw PasswordInvalidException("비밀번호는 8자 이상 255자 이하로 입력해주세요.")
        }

        // 영문자, 숫자, 허용된 특수문자 외의 문자가 포함되어 있으면 안됨
        if (!password.matches("^[a-zA-Z0-9!@#$%^&*()]*$".toRegex())) {
            throw PasswordInvalidException("비밀번호는 영문자, 숫자, 특수문자(!@#$%^&*())만 사용할 수 있습니다.")
        }

        // username과 같은 값이 포함되어 있으면 안됨
        if (password.contains(username)) {
            throw PasswordInvalidException("비밀번호에 아이디가 포함되어 있습니다.")
        }

        // 특수문자 1개 이상 포함
        if (!password.matches(".*[!@#$%^&*()].*".toRegex())) {
            throw PasswordInvalidException("비밀번호에 특수문자를 1개 이상 사용해주세요.")
        }

        // 동일한 영어, 숫자, 허용된 특수문자가 4개 이상 연속되면 안됨
        if (password.matches(".*([a-zA-Z0-9!@#$%^&*()])\\1{3,}.*".toRegex())) {
            throw PasswordInvalidException("비밀번호에 연속된 숫자를 4자 이상 사용할 수 없습니다.")
        }

        return true
    }
}
