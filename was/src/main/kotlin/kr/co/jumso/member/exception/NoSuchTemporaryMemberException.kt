package kr.co.jumso.member.exception

class NoSuchTemporaryMemberException: RuntimeException() {
    override val message = "존재하지 않는 임시 회원입니다."

    override fun toString() = this.message
}
