package com.example.simple_blog.domain.member.entity

import com.example.simple_blog.domain.AuditingEntity
import com.example.simple_blog.domain.auth.entity.RefreshToken
import com.example.simple_blog.domain.company.entity.Company
import com.example.simple_blog.enumstrorage.MemberRole
import com.example.simple_blog.enumstrorage.MemberRole.USER
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.FetchType.LAZY
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Entity
class Member(
    email: String,
    password: String,
    nickname: String,
    companyId: Long,
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

    @Column(nullable = false)
    @NotNull
    var role: MemberRole = USER
        protected set

    @Column(nullable = false)
    @NotNull
    var isDeleted: Boolean = false
        protected set

    @OneToOne(mappedBy = "member", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var refreshToken: RefreshToken? = null
        internal set

    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Company::class)
    var company: Company? = null
        protected set

    @Column(name = "company_id")
    var companyId: Long = companyId
        protected set

    @OneToMany(mappedBy = "member", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var properties: MutableList<MemberProperty> = mutableListOf()
        protected set

    @Column(nullable = false)
    @NotNull
    var lastSignIn: LocalDateTime = now()
        protected set

    // x 좌표, 위도
    var latitude: Double? = null
        protected set

    // y 좌표, 경도
    var longitude: Double? = null
        protected set

    @Column(length = 1000)
    var introduction: String? = null
        protected set

    fun updateLastSignIn() {
        lastSignIn = now()
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun updateIntroduction(introduction: String) {
        this.introduction = introduction
    }
}
