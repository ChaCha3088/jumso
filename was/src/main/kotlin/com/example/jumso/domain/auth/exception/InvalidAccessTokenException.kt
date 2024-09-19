package com.example.jumso.domain.auth.exception

class InvalidAccessTokenException: RuntimeException() {
    override val message: String = "유효하지 않은 Access Token입니다."

    override fun toString() = this.message
}
