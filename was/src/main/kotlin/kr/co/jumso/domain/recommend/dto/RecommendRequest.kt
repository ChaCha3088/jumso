package kr.co.jumso.domain.recommend.dto

import jakarta.validation.constraints.NotNull


data class RecommendRequest(
    @field:NotNull(message = "Property ID는 필수입니다.")
    val propertyIds: Set<Long>,
)
