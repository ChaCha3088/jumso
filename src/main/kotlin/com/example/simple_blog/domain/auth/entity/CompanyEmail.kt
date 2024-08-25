package com.example.simple_blog.domain.auth.entity

import com.example.simple_blog.domain.AuditingEntity
import com.example.simple_blog.domain.company.entity.Company
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotBlank

@Entity
class CompanyEmail(
    address: String,
    company: Company,
): AuditingEntity() {
    @Column(nullable = false, length = 100)
    @NotBlank
    var address: String = address
        protected set

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Company::class)
    var company: Company = company
        protected set
}
