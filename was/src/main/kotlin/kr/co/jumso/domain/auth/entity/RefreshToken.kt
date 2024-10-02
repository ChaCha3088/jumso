package kr.co.jumso.domain.auth.entity

import kr.co.jumso.domain.AuditingEntity
import kr.co.jumso.domain.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToOne
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kr.co.jumso.domain.member.entity.TemporaryMember

@Entity
class RefreshToken(
    token: String,
): AuditingEntity() {
    constructor(
        token: String,
        member: Member
    ): this(token) {
        this.member = member
    }

    constructor(
        token: String,
        temporaryMember: TemporaryMember
    ): this(token) {
        this.temporaryMember = temporaryMember
    }

    @Column(nullable = false, unique = true, length = 400)
    @NotBlank
    var token: String = token
        protected set

    @OneToOne
    var member: Member? = null
        protected set

    @OneToOne
    var temporaryMember: TemporaryMember? = null
        protected set

    fun updateToken(token: String) {
        this.token = token
    }
}
