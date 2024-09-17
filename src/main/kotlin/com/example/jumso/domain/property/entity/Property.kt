package com.example.jumso.domain.property.entity

import com.example.jumso.domain.AuditingEntity
import com.example.jumso.domain.member.entity.MemberProperty
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.FetchType.LAZY
import jakarta.validation.constraints.NotBlank

@Entity
class Property(
    value: String
): AuditingEntity() {
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank
    var value: String = value
        protected set

    @OneToMany(mappedBy = "property", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var memberProperties: MutableList<MemberProperty> = mutableListOf()
        protected set
}
