package kr.co.jumso.domain.member.exception

class NoSuchCompanyException: RuntimeException() {
    override val message = "존재하지 않는 회사입니다."

    override fun toString() = this.message
}
