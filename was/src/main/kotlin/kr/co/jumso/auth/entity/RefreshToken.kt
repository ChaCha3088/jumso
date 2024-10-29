package kr.co.jumso.auth.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToOne
import jakarta.validation.constraints.NotBlank
import kr.co.jumso.AuditingEntity
import kr.co.jumso.member.entity.Member

@Entity
class RefreshToken(
    token: String,
    member: Member,
): AuditingEntity() {
    @Column(nullable = false, unique = true, length = 400)
    @NotBlank
    var token: String = token
        protected set

    @OneToOne
    var member: Member = member
        protected set

    fun updateToken(token: String) {
        this.token = token
    }
}
