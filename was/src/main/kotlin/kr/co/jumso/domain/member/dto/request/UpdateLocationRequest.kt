package kr.co.jumso.domain.member.dto.request

import jakarta.validation.constraints.NotNull

data class UpdateLocationRequest(
    @field:NotNull(message = "위도는 필수 입력 값입니다.")
    val latitude: Double?,

    @field:NotNull(message = "경도는 필수 입력 값입니다.")
    val longitude: Double?,
)
