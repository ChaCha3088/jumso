package kr.co.jumso.domain.member.dto.response

import kr.co.jumso.domain.member.enumstorage.*
import java.time.LocalDateTime

data class MemberRecommendResponse(
    val memberId: Long,
    val nickname: String,
    val lastSignIn: LocalDateTime,
    val companyName: String,
    val memberProperties: List<Long>,
    val bornAt: LocalDateTime,
    val height: Short,
    val bodyType: BodyType,
    val job: String,
    val relationshipStatus: RelationshipStatus,
    val religion: Religion,
    val smoke: Smoke,
    val drink: Drink,
    val introduction: String,
)
