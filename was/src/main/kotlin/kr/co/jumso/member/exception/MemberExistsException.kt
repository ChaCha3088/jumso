package kr.co.jumso.member.exception

class MemberExistsException: RuntimeException() {
    override val message: String = "이메일 인증을 완료해주세요."

    override fun toString() = this.message
}
