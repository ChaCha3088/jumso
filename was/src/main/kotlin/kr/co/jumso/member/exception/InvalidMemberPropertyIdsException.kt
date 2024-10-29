package kr.co.jumso.member.exception

class InvalidMemberPropertyIdsException: RuntimeException() {
    override val message = "유효하지 않은 회원 특성 ID입니다."

    override fun toString() = this.message
}
