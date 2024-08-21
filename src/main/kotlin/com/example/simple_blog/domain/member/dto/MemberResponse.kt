package com.example.simple_blog.domain.member.dto

import com.example.simple_blog.domain.member.entity.Member

data class MemberResponse(
    val email: String,
    val name: String,
    val nickname: String
) {
    constructor(member: Member) : this(
        email = member.email,
        name = member.name,
        nickname = member.nickname
    )
}
