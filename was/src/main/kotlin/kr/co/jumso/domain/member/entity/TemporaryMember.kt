package kr.co.jumso.domain.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kr.co.jumso.domain.AuditingEntity
import kr.co.jumso.domain.auth.entity.CompanyEmail
import kr.co.jumso.domain.member.exception.InvalidVerificationCodeException
import java.util.UUID.randomUUID

@Entity
class TemporaryMember(
    email: String,
    password: String,
    nickname: String,
    companyEmailId: Long,
): AuditingEntity() {
    @Column(nullable = false, unique = true)
    @NotBlank
    var email: String = email
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
    var verificationCode: String = randomUUID().toString()
        protected set

    @Column(nullable = false)
    @NotNull
    var verified: Boolean = false
        protected set

    fun verify(verificationInput: String) {
        if (!verified && verificationInput == verificationCode) {
            verified = true
        } else {
            throw InvalidVerificationCodeException()
        }
    }
}
