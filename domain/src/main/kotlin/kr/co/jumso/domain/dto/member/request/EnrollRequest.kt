package kr.co.jumso.domain.dto.member.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kr.co.jumso.domain.member.enumstorage.*
import java.time.LocalDateTime

data class EnrollRequest(
    @field:NotNull(message = "생년월일을 입력해주세요.")
    val bornAt: LocalDateTime,

    @field:NotNull(message = "성별을 선택해주세요.")
    val sex: Sex,

    @field:NotNull(message = "키를 입력해주세요.")
    @field:Min(value = 100, message = "키는 100cm 이상으로 입력해주세요.")
    @field:Max(value = 300, message = "키는 300cm 이하로 입력해주세요.")
    val height: Short,

    @field:NotNull(message = "체형을 선택해주세요.")
    val bodyType: BodyType,

    @field:NotBlank(message = "직업을 입력해주세요.")
    val job: String,

    @field:NotNull(message = "교제 상태를 선택해주세요.")
    val relationshipStatus: RelationshipStatus,

    @field:NotNull(message = "종교를 선택해주세요.")
    val religion: Religion,

    @field:NotNull(message = "흡연 여부를 선택해주세요.")
    val smoke: Smoke,

    @field:NotNull(message = "음주 여부를 선택해주세요.")
    val drink: Drink,

    @field:NotNull(message = "위치를 선택해주세요.")
    val latitude: Double,

    @field:NotNull(message = "위치를 선택해주세요.")
    val longitude: Double,

    @field:NotBlank(message = "자기소개를 입력해주세요.")
    val introduction: String,

    @field:NotNull(message = "원하는 성별을 선택해주세요.")
    val whatSexDoYouWant: Sex,

    @field:NotNull(message = "원하는 키를 입력해주세요.")
    @field:Min(value = 100, message = "키는 100cm 이상으로 입력해주세요.")
    @field:Max(value = 300, message = "키는 300cm 이하로 입력해주세요.")
    val howTallDoYouWantMin: Short,

    @field:NotNull(message = "원하는 키를 입력해주세요.")
    @field:Min(value = 100, message = "키는 100cm 이상으로 입력해주세요.")
    @field:Max(value = 300, message = "키는 300cm 이하로 입력해주세요.")
    val howTallDoYouWantMax: Short,

    @field:NotNull(message = "원하는 나이를 선택해주세요.")
    @field:Max(value = 127, message = "나이는 127 이하로 입력해주세요.")
    @field:Min(value = 0, message = "나이는 0 이상으로 입력해주세요.")
    val howOldDoYouWantMin: Byte,

    @field:NotNull(message = "원하는 나이를 선택해주세요.")
    @field:Max(value = 127, message = "나이는 127 이하로 입력해주세요.")
    @field:Min(value = 0, message = "나이는 0 이상으로 입력해주세요.")
    val howOldDoYouWantMax: Byte,

    @field:NotNull(message = "원하는 거리를 입력해주세요.")
    @field:Max(value = 127, message = "거리는 127 이하로 입력해주세요.")
    @field:Min(value = 0, message = "거리는 0 이상으로 입력해주세요.")
    val howFarCanYouGo: Byte,

    @field:NotNull(message = "원하는 체형을 선택해주세요.")
    val whatKindOfBodyTypeDoYouWant: BodyType,

    @field:NotNull(message = "원하는 교제 상태를 선택해주세요.")
    val whatKindOfRelationshipStatusDoYouWant: RelationshipStatus,

    @field:NotNull(message = "원하는 종교를 선택해주세요.")
    val whatKindOfReligionDoYouWant: Religion,

    @field:NotNull(message = "원하는 흡연 여부를 선택해주세요.")
    val whatKindOfSmokeDoYouWant: Smoke,

    @field:NotNull(message = "원하는 음주 여부를 선택해주세요.")
    val whatKindOfDrinkDoYouWant: Drink,

    @field:NotNull(message = "특성을 하나 이상씩 선택해주세요.")
    val propertyIds: Set<Long>,

    @field:NotNull(message = "만나기 싫은 회사를 선택해주세요.")
    val notTheseCompanyIds: Set<Long>,
)
