package kr.co.jumso.member.service

import kr.co.jumso.dto.member.request.MemberPropertyRequest
import kr.co.jumso.member.entity.MemberProperty
import kr.co.jumso.member.exception.NoSuchMemberException
import kr.co.jumso.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberPropertyService(
    private val memberRepository: MemberRepository,
) {
    @Transactional
    fun putProperties(memberId: Long, memberPropertyRequest: MemberPropertyRequest) {
        val memberWithMemberProperties = memberRepository.findNotDeletedByIdWithMemberProperties(memberId)
            ?: throw NoSuchMemberException()

        memberWithMemberProperties.addMemberProperties(memberPropertyRequest.propertyIds)

        memberRepository.save(memberWithMemberProperties)
    }
}
