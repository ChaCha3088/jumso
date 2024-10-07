package kr.co.jumso.domain.member.dto.response

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
