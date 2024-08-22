package com.example.simple_blog.domain.member.dto

import jakarta.validation.constraints.NotNull

data class MemberPropertyRequest(
    @field:NotNull(message = "Property ID는 필수입니다.")
    val propertyIds: Set<Long>?
)
