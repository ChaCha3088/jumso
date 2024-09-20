package kr.co.jumso.domain.member.dto

import kr.co.jumso.domain.member.entity.Member

data class MemberResponse(
    val email: String,
    val nickname: String
) {
    constructor(member: Member) : this(
        email = member.email,
        nickname = member.nickname
    )
}
