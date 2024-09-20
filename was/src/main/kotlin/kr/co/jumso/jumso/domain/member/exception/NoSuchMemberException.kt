package kr.co.jumso.domain.member.exception

class NoSuchMemberException: RuntimeException() {
    override val message = "존재하지 않는 회원입니다."

    override fun toString() = this.message
}
