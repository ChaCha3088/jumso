package kr.co.jumso.recommend.service

import kr.co.jumso.dto.member.response.MemberRecommendResponse
import kr.co.jumso.member.exception.NoSuchMemberException
import kr.co.jumso.member.repository.MemberPropertyRepository
import kr.co.jumso.member.repository.MemberRepository
import kr.co.jumso.util.CoordinateConverter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RecommendService(
    private val memberRepository: MemberRepository,
    private val memberPropertyRepository: MemberPropertyRepository,

    private val coordinateConverter: CoordinateConverter,
) {
    fun getRecommendList(
        memberId: Long,
        requestPropertyIds: Set<Long>,
    ): List<MemberRecommendResponse> {
        // requestPropertyIds의 최대 개수는 10개
        if (requestPropertyIds.size > 10) {
            throw IllegalArgumentException("PropertyId의 최대 개수는 10개입니다.")
        }

        val member = memberRepository
            .findNotDeletedByIdWithNotTheseCompaniesAndNoMatch(memberId)
            ?: throw NoSuchMemberException()

        // member가 원하는 Property 갖고 있는 memberId들 조회
        val memberIds = memberPropertyRepository.findByCriteria(
            memberId = memberId,
            propertyIds = requestPropertyIds,
        )

        return memberRepository.findNotDeletedByCriteria(
            // In memberIds
            memberIds = memberIds,

            // NotIn NotTheseCompanies
            notTheseCompanyIds = member.notTheseCompanies.map { it.id!! },

            // NotIn No Match
            noMatchIds = member.noMatch.map { it.id!! },

            // sex
            whatSexDoYouWant = member.whatSexDoYouWant,

            // height
            howTallDoYouWantMin = member.howTallDoYouWantMin,
            howTallDoYouWantMax = member.howTallDoYouWantMax,

            // age
            howOldDoYouWantMin = member.howOldDoYouWantMin,
            howOldDoYouWantMax = member.howOldDoYouWantMax,

            // coordinates
            coordinates = coordinateConverter.coordinates(
                member.latitude,
                member.longitude,
                member.howFarCanYouGo,
            ),

            // bodyType
            whatKindOfBodyTypeDoYouWant = member.whatKindOfBodyTypeDoYouWant,

            // relationshipStatus
            whatKindOfRelationshipStatusDoYouWant = member.whatKindOfRelationshipStatusDoYouWant,

            // religion
            whatKindOfReligionDoYouWant = member.whatKindOfReligionDoYouWant,

            // smoke
            whatKindOfSmokeDoYouWant = member.whatKindOfSmokeDoYouWant,

            // drink
            whatKindOfDrinkDoYouWant = member.whatKindOfDrinkDoYouWant,
        ).map {
            MemberRecommendResponse(
                memberId = it.id!!,
                nickname = it.nickname,
                lastSignIn = it.lastSignIn,
                companyName = it.company!!.name,
                memberProperties = it.memberProperties.map { memberProperty -> memberProperty.id!! },
                bornAt = it.bornAt,
                height = it.height,
                bodyType = it.bodyType,
                job = it.job,
                relationshipStatus = it.relationshipStatus,
                religion = it.religion,
                smoke = it.smoke,
                drink = it.drink,
                introduction = it.introduction,
            )
        }
    }
}
