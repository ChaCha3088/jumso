package kr.co.jumso.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import kr.co.jumso.AuditingEntity
import kr.co.jumso.property.entity.Property

@Entity
class MemberProperty(
    memberId: Long,
    propertyId: Long,
): AuditingEntity() {
    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Member::class)
    var member: Member? = null
        protected set

    @Column(name = "member_id")
    var memberId: Long = memberId
        protected set

    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Property::class)
    var property: Property? = null
        protected set

    @Column(name = "property_id")
    var propertyId = propertyId
        protected set

    override fun hashCode(): Int {
        return memberId.hashCode() + propertyId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as MemberProperty

        return memberId == other.memberId && propertyId == other.propertyId
    }
}
