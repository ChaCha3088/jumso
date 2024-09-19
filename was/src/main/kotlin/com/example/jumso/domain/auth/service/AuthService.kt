package com.example.jumso.domain.auth.service

import com.example.jumso.domain.auth.dto.SignInRequest
import com.example.jumso.domain.auth.exception.InvalidPasswordException
import com.example.jumso.domain.auth.repository.RefreshTokenRepository
import com.example.jumso.domain.member.dto.MemberResponse
import com.example.jumso.domain.member.entity.Member
import com.example.jumso.domain.member.exception.NoSuchMemberException
import com.example.jumso.domain.member.repository.MemberRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val jwtService: JwtService,
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun signIn(
        signInRequest: SignInRequest,
        response: HttpServletResponse
    ): MemberResponse {
        // Member가 이미 있는지 확인한다.
        val member: Member = memberRepository.findNotDeletedByEmailWithRefreshToken(signInRequest.email!!)
            ?: throw NoSuchMemberException()

        // Member가 있으면, 비밀번호가 맞는지 확인한다.
        if (!passwordEncoder.matches(signInRequest.password, member.password)) {
            throw InvalidPasswordException()
        }

        // 새로운 토큰을 발급한다.
        val jwts: Array<String> = jwtService.issueJwts(member)

        setHeader(response, jwts)

        // member를 반환
        return MemberResponse(member)
    }

    @Transactional
    fun reissue(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        // Header에서 Refresh Token 추출
        val refreshToken: String = jwtService.extractRefreshToken(request)

        val memberId: Long = jwtService.validateAndExtractMemberIdFromRefreshToken(refreshToken)

        // token들을 재발급한다.
        val jwts: Array<String> = jwtService.reissueJwts(memberId, refreshToken)

        setHeader(response, jwts)
    }

//    // Transactional 필요 없음
//    fun reissueJwts(request: HttpServletRequest?): Array<String> {
////        , NoSuchDeviceTokenException
////        // Header에서 deviceToken 추출
////        String deviceToken = deviceTokenService.extractDeviceTokenFromHeader(request);
//
//        // ToDo: 테스트 할 때만 끔 // 추출한 deviceToken이 유효한지 확인한다.
////        deviceTokenService.validateAndExtractDeviceToken(deviceToken);
//
//        // Header에서 refreshToken 추출
//
//        val refreshToken: String = jwtService.extractRefreshToken(request)
//        val memberIdFromRefreshToken: Long = jwtService.validateAndExtractMemberIdFromRefreshToken(
//            refreshToken
//        )
//
//        // token들을 재발급한다.
//        val jwts: Array<String> = jwtService.reissueJwts(memberIdFromRefreshToken, refreshToken)
//
//        //        , deviceToken);
//
//        // Redis에 RefreshToken 저장
//        jwtService.saveRefreshTokenToRedis(jwts[1], memberIdFromRefreshToken)
//
//        return jwts
//    }
//
    @Transactional
    fun signOut(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        // Header에서 Refresh Token 추출
        val refreshToken: String = jwtService.extractRefreshToken(request)

        val memberId: Long = jwtService.validateAndExtractMemberIdFromRefreshToken(refreshToken)

        refreshTokenRepository.deleteByMemberId(memberId)

        // Header에서 Access Token 삭제
        jwtService.setAccessTokenOnHeader(response, null)

        // Header에서 Refresh Token 삭제
        jwtService.setRefreshTokenOnHeader(response, null)
    }

    private fun setHeader(response: HttpServletResponse, jwts: Array<String>) {
        jwtService.setAccessTokenOnHeader(response, jwts[0])
        jwtService.setRefreshTokenOnHeader(response, jwts[1])
    }
}
