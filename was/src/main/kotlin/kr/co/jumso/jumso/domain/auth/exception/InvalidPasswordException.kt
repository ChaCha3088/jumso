package kr.co.jumso.domain.auth.exception

class InvalidPasswordException: RuntimeException() {
    override val message = "비밀번호가 일치하지 않습니다."

    override fun toString() = this.message
}
