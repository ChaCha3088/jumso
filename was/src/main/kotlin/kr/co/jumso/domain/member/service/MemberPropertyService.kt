package kr.co.jumso.domain.member.service

import kr.co.jumso.domain.member.dto.request.MemberPropertyRequest
import kr.co.jumso.domain.member.entity.MemberProperty
import kr.co.jumso.domain.member.exception.InvalidMemberPropertyIdsException
import kr.co.jumso.domain.member.repository.MemberPropertyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberPropertyService(
    private val memberPropertyRepository: MemberPropertyRepository
) {
    @Transactional
    fun create(memberId: Long, memberPropertyRequest: MemberPropertyRequest) {
        val memberProperties = memberPropertyRepository.findByMemberId(memberId)

        // propertyId가 memberProperties에 들어있지 않은 경우에만 추가
        val newIds = memberPropertyRequest.propertyIds!!
        val oldIds = memberProperties.map { it.propertyId }.toSet()

        val diff = newIds - oldIds

        memberProperties.addAll(
            diff.map {
                MemberProperty(
                    memberId,
                    it
                )
            }
        )

        // memberPropertyIds가 1000번대, 2000번대, 3000번대가 하나 이상, 5개 이하인지 확인
        val propertyIds1000 = mutableSetOf<Long>()
        val propertyIds2000 = mutableSetOf<Long>()
        val propertyIds3000 = mutableSetOf<Long>()

        for (memberProperty in memberProperties) {
            when (memberProperty.id!!) {
                in 1000..1999 -> propertyIds1000.add(memberProperty.id!!)
                in 2000..2999 -> propertyIds2000.add(memberProperty.id!!)
                in 3000..3999 -> propertyIds3000.add(memberProperty.id!!)
                else -> {}
            }
        }

        if (propertyIds1000.size !in 1..5 || propertyIds2000.size !in 1..5 || propertyIds3000.size !in 1..5) {
            throw InvalidMemberPropertyIdsException()
        }

        memberPropertyRepository.saveAll(memberProperties)
    }
}
