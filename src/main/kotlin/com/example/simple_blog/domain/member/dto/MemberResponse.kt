package com.example.simple_blog.domain.member.dto

import com.example.simple_blog.domain.member.entity.Member

data class MemberResponse(
    val email: String,
    val nickname: String
) {
    constructor(member: Member) : this(
        email = member.email,
        nickname = member.nickname
    )
}
