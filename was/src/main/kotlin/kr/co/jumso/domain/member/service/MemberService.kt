package kr.co.jumso.domain.member.service

import kr.co.jumso.domain.member.dto.request.UpdateIntroductionRequest
import kr.co.jumso.domain.member.dto.request.UpdateLocationRequest
import kr.co.jumso.domain.member.exception.NoSuchMemberException
import kr.co.jumso.domain.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
) {
    @Transactional
    fun updateLocation(memberId: Long, updateLocationRequest: UpdateLocationRequest) {
        val member = memberRepository.findNotDeletedById(memberId)
            ?: throw NoSuchMemberException()

        member.updateLocation(updateLocationRequest.latitude!!, updateLocationRequest.longitude!!)

        memberRepository.save(member)
    }

    @Transactional
    fun updateIntroduce(memberId: Long, updateIntroductionRequest: UpdateIntroductionRequest) {
        val member = memberRepository.findNotDeletedById(memberId)
            ?: throw NoSuchMemberException()

        member.updateIntroduction(updateIntroductionRequest.introduction!!)

        memberRepository.save(member)
    }
}
