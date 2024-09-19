package com.example.jumso.domain.auth.security

import com.example.jumso.domain.member.entity.Member
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val member: Member
) : UserDetails, Authentication {
    override fun getName(): String {
        return member.email
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(member.role).map { SimpleGrantedAuthority(it.name) }
    }

    override fun getCredentials(): Any {
        return member.password
    }

    override fun getDetails(): Any {
        return member
    }

    override fun getPrincipal(): Any {
        return member
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        // do nothing
    }

    override fun getPassword(): String {
        return member.password
    }

    override fun getUsername(): String {
        return member.email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return !member.isDeleted
    }
}
