package kr.co.jumso.domain.member.dto.request

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
