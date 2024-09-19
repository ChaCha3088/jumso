package com.example.jumso.domain.auth.exception

class CompanyEmailNotFoundException: RuntimeException() {
    override val message: String = "회사 이메일을 찾을 수 없습니다."

    override fun toString() = this.message
}
