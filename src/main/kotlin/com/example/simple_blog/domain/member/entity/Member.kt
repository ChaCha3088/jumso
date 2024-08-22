package com.example.simple_blog.domain.member.entity

import com.example.simple_blog.domain.AuditingEntity
import com.example.simple_blog.domain.auth.entity.RefreshToken
import com.example.simple_blog.enumstrorage.MemberRole
import com.example.simple_blog.enumstrorage.MemberRole.USER
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.FetchType.LAZY
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Entity
class Member(
    email: String,
    password: String,
    name: String,
    nickname: String
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
    var name: String = name
        protected set

    @Column(nullable = false)
    @NotBlank
    var nickname: String = nickname
        protected set

    @Column(nullable = false)
    @NotNull
    var role: MemberRole = USER
        protected set

    @Column(nullable = false)
    @NotNull
    var isDeleted: Boolean = false
        protected set

    // OneToOne로 RefreshToken을 관리한다.
    @OneToOne(mappedBy = "member", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var refreshToken: RefreshToken? = null
        internal set

    @OneToMany(mappedBy = "member", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var properties: MutableList<MemberProperty> = mutableListOf()
        protected set
}
