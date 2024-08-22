package com.example.simple_blog.domain.member.service

import com.example.simple_blog.domain.member.dto.MemberPropertyRequest
import com.example.simple_blog.domain.member.entity.MemberProperty
import com.example.simple_blog.domain.member.repository.MemberPropertyRepository
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

        memberPropertyRepository.saveAll(memberProperties)
    }
}
