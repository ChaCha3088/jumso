package kr.co.jumso.auth.security

import kr.co.jumso.member.repository.MemberRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val memberRepository: MemberRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val member = memberRepository.findNotDeletedByEmail(username)
            ?: throw UsernameNotFoundException("User not found")

        return CustomUserDetails(member)
    }
}
