package kr.co.jumso.domain.dto.member.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CompanyChangeRequest(
    @field:NotNull
    val companyId: Long,

    @field:NotBlank
    val newDomain: String,

    @field:NotBlank
    val newUsername: String,
)
