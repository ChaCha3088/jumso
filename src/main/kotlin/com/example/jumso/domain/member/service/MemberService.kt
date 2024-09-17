package com.example.jumso.domain.member.service

import com.example.jumso.domain.member.dto.UpdateIntroductionRequest
import com.example.jumso.domain.member.dto.UpdateLocationRequest
import com.example.jumso.domain.member.exception.NoSuchMemberException
import com.example.jumso.domain.member.repository.MemberRepository
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
