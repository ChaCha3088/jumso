package com.example.jumso.domain.member.entity

import com.example.jumso.domain.AuditingEntity
import com.example.jumso.domain.property.entity.Property
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY

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
}