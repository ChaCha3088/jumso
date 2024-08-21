package com.example.simple_blog.domain.member.service

import com.example.simple_blog.domain.member.dto.MemberRequest
import com.example.simple_blog.domain.member.dto.MemberResponse
import com.example.simple_blog.domain.member.repository.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun findAll(): List<MemberResponse> = memberRepository.findNotDeletedMembers().map { MemberResponse(it) }.toList()

    @Transactional
    fun create(memberRequest: MemberRequest) {
        memberRepository.save(memberRequest.toEntity(passwordEncoder))
    }
}
