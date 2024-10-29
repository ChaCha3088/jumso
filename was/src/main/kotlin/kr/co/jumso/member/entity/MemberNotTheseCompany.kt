package kr.co.jumso.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import kr.co.jumso.AuditingEntity
import kr.co.jumso.company.entity.Company

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

    override fun hashCode(): Int {
        return memberId.hashCode() + companyId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MemberNotTheseCompany

        if (memberId != other.memberId) return false
        if (companyId != other.companyId) return false

        return true
    }
}
