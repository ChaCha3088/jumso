package kr.co.jumso.domain.member.entity

import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import kr.co.jumso.domain.AuditingEntity
import kr.co.jumso.domain.auth.entity.CompanyEmail
import kr.co.jumso.domain.member.exception.InvalidVerificationCodeException
import jakarta.persistence.FetchType.LAZY
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kr.co.jumso.domain.auth.entity.RefreshToken
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

    @NotNull
    var verified: Boolean = false
        protected set

    @OneToOne(mappedBy = "temporaryMember", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var refreshToken: RefreshToken? = null
        internal set

    fun verify(verificationInput: String) {
        if (!verified && verificationInput == verificationCode) {
            verified = true
        } else {
            throw InvalidVerificationCodeException()
        }
    }
}
