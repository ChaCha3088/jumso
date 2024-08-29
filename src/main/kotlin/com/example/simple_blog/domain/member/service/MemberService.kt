package com.example.simple_blog.domain.member.service

import com.example.simple_blog.domain.member.dto.TemporaryMemberRequest
import com.example.simple_blog.domain.member.dto.MemberResponse
import com.example.simple_blog.domain.member.dto.UpdateLocationRequest
import com.example.simple_blog.domain.member.exception.NoSuchMemberException
import com.example.simple_blog.domain.member.repository.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
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
}
