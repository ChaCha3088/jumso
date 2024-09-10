package com.example.simple_blog.domain.member.entity

import com.example.simple_blog.domain.AuditingEntity
import com.example.simple_blog.domain.company.entity.Company
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY

@Entity
class MemberNotTheseCompany(
    memberId: Long,
    companyId: Long,
): AuditingEntity() {
    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Member::class)
    var member: Member? = null
        protected set

    @Column(name = "member_id")
    var memberId: Long = memberId
        protected set

    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Company::class)
    var company: Company? = null
        protected set

    @Column(name = "company_id")
    var companyId: Long = companyId
        protected set
}
