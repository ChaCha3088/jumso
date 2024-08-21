package com.example.simple_blog.domain.auth.entity

import com.example.simple_blog.domain.AuditingEntity
import com.example.simple_blog.domain.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToOne
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Entity
class RefreshToken(
    token: String,
    member: Member
): AuditingEntity() {
    @Column(nullable = false, unique = true, length = 400)
    @NotBlank
    var token: String = token
        protected set

    @OneToOne
    @NotNull
    var member: Member = member
        protected set

    init {
        member.refreshToken = this
    }

    fun updateToken(token: String) {
        this.token = token
    }
}
