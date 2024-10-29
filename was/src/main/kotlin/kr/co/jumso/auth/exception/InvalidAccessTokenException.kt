package kr.co.jumso.auth.exception

class InvalidAccessTokenException: RuntimeException() {
    override val message: String = "유효하지 않은 Access Token입니다."

    override fun toString() = this.message
}
