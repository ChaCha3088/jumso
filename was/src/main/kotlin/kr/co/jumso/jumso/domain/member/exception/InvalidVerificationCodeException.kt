package kr.co.jumso.domain.member.exception

class InvalidVerificationCodeException: RuntimeException() {
    override val message = "유효하지 않은 인증 코드입니다."

    override fun toString() = this.message
}
