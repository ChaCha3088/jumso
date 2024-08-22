package com.example.simple_blog.domain.member.entity

import com.example.simple_blog.domain.AuditingEntity
import com.example.simple_blog.domain.property.entity.Property
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["member_id", "property_id"]
        )
    ]
)
class MemberProperty(
    memberId: Long,
    propertyId: Long,
): AuditingEntity() {
    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Member::class)
    var member: Member? = null
        protected set

    @Column(name = "member_id")
    var memberId = memberId
        protected set

    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Property::class)
    var property: Property? = null
        protected set

    @Column(name = "property_id")
    var propertyId = propertyId
        protected set
}
