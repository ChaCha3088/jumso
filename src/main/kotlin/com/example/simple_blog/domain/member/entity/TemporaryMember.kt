package com.example.simple_blog.domain.member.entity

import com.example.simple_blog.domain.AuditingEntity
import com.example.simple_blog.domain.auth.entity.CompanyEmail
import com.example.simple_blog.domain.member.exception.InvalidVerificationCodeException
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotBlank
import java.util.*

@Entity
class TemporaryMember(
    username: String,
    password: String,
    nickname: String,
    companyEmailId: Long,
): AuditingEntity() {
    @Column(nullable = false)
    @NotBlank
    var username: String = username
        protected set

    @Column(nullable = false)
    @NotBlank
    var password: String = password
        protected set

    @Column(nullable = false)
    @NotBlank
    var nickname: String = nickname
        protected set

    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = CompanyEmail::class)
    var companyEmail: CompanyEmail? = null
        protected set

    @Column(name = "company_email_id")
    var companyEmailId: Long = companyEmailId
        protected set

    @Column(nullable = false, unique = true, length = 36)
    @NotBlank
    var verificationCode: String = UUID.randomUUID().toString()
        protected set

    fun verify(verificationInput: String): Member {
        if (verificationInput == verificationCode) {
            return Member(
                username + "@" + companyEmail!!.address,
                password,
                nickname,
                companyEmail!!.company.id!!
            )
        } else {
            throw InvalidVerificationCodeException()
        }
    }
}
