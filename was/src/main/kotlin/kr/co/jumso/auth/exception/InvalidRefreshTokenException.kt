package kr.co.jumso.auth.exception

class InvalidRefreshTokenException: RuntimeException() {
    override val message: String = "유효하지 않은 Refresh Token입니다."

    override fun toString() = this.message
}
