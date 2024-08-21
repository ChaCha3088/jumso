package com.example.simple_blog.domain.property.entity

import com.example.simple_blog.domain.AuditingEntity
import com.example.simple_blog.domain.member.entity.Member
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import jakarta.validation.constraints.NotNull

@Entity
class MemberProperty(
    memberId: Long,
    propertyId: Long,
): AuditingEntity() {
    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(targetEntity = Member::class)
    var member: Member? = null
        protected set

    @Column(name = "member_id")
    var memberId = memberId
        protected set

    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(targetEntity = Property::class)
    var property: Property? = null
        protected set

    @Column(name = "property_id")
    var propertyId = propertyId
        protected set
}