package kr.co.jumso.domain.member.entity

import kr.co.jumso.domain.AuditingEntity
import kr.co.jumso.domain.company.entity.Company
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

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
