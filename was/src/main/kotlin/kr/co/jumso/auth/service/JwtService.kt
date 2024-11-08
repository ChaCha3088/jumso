package kr.co.jumso.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWT.require
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm.HMAC512
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.co.jumso.auth.entity.RefreshToken
import kr.co.jumso.auth.exception.InvalidAccessTokenException
import kr.co.jumso.auth.exception.InvalidRefreshTokenException
import kr.co.jumso.auth.repository.RefreshTokenRepository
import kr.co.jumso.member.entity.Member
import kr.co.jumso.member.entity.TemporaryMember
import kr.co.jumso.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.System.currentTimeMillis
import java.util.*

@Service
// Transactional 붙이지 마
class JwtService(
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    @Value("\${jwt.issuer}")
    private lateinit var issuer: String

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.token.access.expiration}")
    private var accessTokenExpiration: Long = 0

    @Value("\${jwt.token.refresh.expiration}")
    private var refreshTokenExpiration: Long = 0

    private lateinit var memberAccessTokenDecoder: JWTVerifier
    private lateinit var memberRefreshTokenDecoder: JWTVerifier
    private lateinit var temporaryMemberAccessTokenDecoder: JWTVerifier

    @PostConstruct
    fun init() {
        memberAccessTokenDecoder = require(HMAC512(secret))
            .withIssuer(issuer)
            .withClaimPresence("memberId")
            .withSubject("accessToken")
            .build() // 반환된 빌더로 JWT verifier 생성

        memberRefreshTokenDecoder = require(HMAC512(secret))
            .withIssuer(issuer)
            .withClaimPresence("memberId")
            .withSubject("refreshToken")
            .build() // 반환된 빌더로 JWT verifier 생성

        temporaryMemberAccessTokenDecoder = require(HMAC512(secret))
            .withIssuer(issuer)
            .withClaimPresence("temporaryMemberId")
            .withSubject("accessToken")
            .build() // 반환된 빌더로 JWT verifier 생성
    }

    // access token, refresh token을 발급, DB에 저장한다.
    @Transactional
    fun issueMemberJwts(member: Member): Array<String> {
        // 마지막 로그인 시간 업데이트
        member.updateLastSignIn()

        // refresh token을 발급한다.
        val newRefreshToken: String = JWT.create()
            .withIssuer(issuer)
            .withSubject("refreshToken")
            .withIssuedAt(Date(currentTimeMillis()))
            .withExpiresAt(Date(currentTimeMillis() + refreshTokenExpiration))
            .withClaim("memberId", member.id.toString())
            .withClaim("issuedTime", currentTimeMillis())
            .sign(HMAC512(secret))

        // refreshToken이 있으면, 업데이트
        member.refreshToken?.updateToken(newRefreshToken)
            // refreshToken이 없으면, 새로 저장
            ?: refreshTokenRepository.save(
                RefreshToken(newRefreshToken, member)
            )

        // access token을 발급한다.
        val newAccessToken: String = JWT.create()
            .withIssuer(issuer)
            .withSubject("accessToken")
            .withIssuedAt(Date(currentTimeMillis()))
            .withExpiresAt(Date(currentTimeMillis() + accessTokenExpiration))
            .withClaim("memberId", member.id.toString())
            .withClaim("issuedTime", currentTimeMillis())
            .sign(HMAC512(secret))

        return arrayOf(newAccessToken, newRefreshToken)
    }

    @Transactional
    fun issueTemporaryMemberJwts(temporaryMember: TemporaryMember): String {
        // access token을 발급한다.
        val newAccessToken: String = JWT.create()
            .withIssuer(issuer)
            .withSubject("accessToken")
            .withIssuedAt(Date(currentTimeMillis()))
            .withExpiresAt(Date(currentTimeMillis() + accessTokenExpiration))
            .withClaim("temporaryMemberId", temporaryMember.id.toString())
            .withClaim("issuedTime", currentTimeMillis())
            .sign(HMAC512(secret))

        return newAccessToken
    }

    @Transactional
    fun reissueJwts(memberId: Long, refreshToken: String): Array<String> {
        // Member 조회
        val member: Member = memberRepository.findNotDeletedWithRefreshTokenByIdAndRefreshToken(memberId, refreshToken)
            // 없으면 꺼져
            ?: throw InvalidRefreshTokenException()

        // 마지막 로그인 시간 업데이트
        member.updateLastSignIn()

        // refresh token을 발급한다.
        val newRefreshToken: String = JWT.create()
            .withIssuer(issuer)
            .withSubject("refreshToken")
            .withIssuedAt(Date(currentTimeMillis()))
            .withExpiresAt(Date(currentTimeMillis() + refreshTokenExpiration))
            .withClaim("memberId", memberId.toString())
            .withClaim("issuedTime", currentTimeMillis())
            .sign(HMAC512(secret))

        // Refresh Token Entity 업데이트
        member.refreshToken!!.updateToken(newRefreshToken)

        // access token을 발급한다.
        val newAccessToken: String = JWT.create()
            .withIssuer(issuer)
            .withSubject("accessToken")
            .withIssuedAt(Date(currentTimeMillis()))
            .withExpiresAt(Date(currentTimeMillis() + accessTokenExpiration))
            .withClaim("memberId", memberId.toString())
            .withClaim("issuedTime", currentTimeMillis())
            .sign(HMAC512(secret))

        return arrayOf(newAccessToken, newRefreshToken)
    }

    fun extractAccessTokenFromHeader(request: HttpServletRequest): String {
        val accessToken = request.getHeader(ACCESS_TOKEN_HEADER)

        if (accessToken.isNullOrBlank()) {
            throw InvalidAccessTokenException()
        }

        return accessToken.replace(BEARER, "")
    }

    fun extractRefreshTokenFromHeader(request: HttpServletRequest): String {
        val refreshToken = request.getHeader(REFRESH_TOKEN_HEADER)

        // refreshToken 값 검증
        if (refreshToken.isNullOrBlank()) {
            throw InvalidRefreshTokenException()
        }

        return refreshToken.replace(BEARER, "")
    }

    fun validateAndExtractMemberIdFromAccessToken(accessToken: String): Long {
        try {
            // accessToken 값 검증
            val jwt: DecodedJWT = memberAccessTokenDecoder
                .verify(accessToken) // accessToken을 검증하고 유효하지 않다면 예외 발생

            return jwt
                .getClaim("memberId")
                .asString().toLong() // claim(MemberId) 가져오기
        } catch (e: JWTVerificationException) {
            throw InvalidAccessTokenException()
        }
    }

    fun validateAndExtractMemberIdFromRefreshToken(refreshToken: String): Long {
        try {
            // refreshToken을 검증한다.
            val jwt: DecodedJWT = memberRefreshTokenDecoder
                .verify(refreshToken)

            return jwt
                .getClaim("memberId")
                .asString().toLong()
        } catch (e: JWTVerificationException) {
            throw InvalidRefreshTokenException()
        }
    }

    fun validateAndExtractTemporaryMemberIdFromAccessToken(accessToken: String): Long {
        try {
            // accessToken 값 검증
            val jwt: DecodedJWT = temporaryMemberAccessTokenDecoder
                .verify(accessToken) // accessToken을 검증하고 유효하지 않다면 예외 발생

            return jwt
                .getClaim("temporaryMemberId")
                .asString().toLong() // claim(MemberId) 가져오기
        } catch (e: JWTVerificationException) {
            throw InvalidAccessTokenException()
        }
    }

    fun validateAndExtractTemporaryMemberIdFromRefreshToken(refreshToken: String): Long {
        try {
            // refreshToken을 검증한다.
            val jwt: DecodedJWT = memberRefreshTokenDecoder
                .verify(refreshToken)

            return jwt
                .getClaim("temporaryMemberId")
                .asString().toLong()
        } catch (e: JWTVerificationException) {
            throw InvalidRefreshTokenException()
        }
    }

    /**
     * accessToken을 Header에 작성한다.
     */
    fun setAccessTokenOnHeader(response: HttpServletResponse, token: String?) {
        // token이 null이 아니면, Header에 작성
        if (token != null) {
            response.setHeader(ACCESS_TOKEN_HEADER, BEARER + token)
        }
        // token이 null이면, Header에서 삭제
        else {
            response.setHeader(ACCESS_TOKEN_HEADER, "")
        }
    }

    /**
     * refreshToken을 Header에 작성한다.
     */
    fun setRefreshTokenOnHeader(response: HttpServletResponse, token: String?) {
        // token이 null이 아니면, Header에 작성
        if (token != null) {
            response.setHeader(REFRESH_TOKEN_HEADER, BEARER + token)
        }
        // token이 null이면, Header에서 삭제
        else {
            response.setHeader(REFRESH_TOKEN_HEADER, "")
        }
    }

    companion object {
        const val ACCESS_TOKEN_HEADER: String = "Authorization"

        const val REFRESH_TOKEN_HEADER: String = "AuthorizationRefresh"

        const val BEARER: String = "Bearer "

        const val REFRESH_TOKEN_REDIS_KEY: String = "Auth:RefreshToken:"
    }
}
