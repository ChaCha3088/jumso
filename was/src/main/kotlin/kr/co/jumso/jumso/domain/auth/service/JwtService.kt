package kr.co.jumso.domain.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm.HMAC512
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.jumso.domain.auth.entity.RefreshToken
import com.example.jumso.domain.auth.exception.InvalidAccessTokenException
import com.example.jumso.domain.auth.exception.InvalidRefreshTokenException
import com.example.jumso.domain.auth.repository.RefreshTokenRepository
import com.example.jumso.domain.member.entity.Member
import com.example.jumso.domain.member.repository.MemberRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

    // access token, refresh token을 발급, DB에 저장한다.
    @Transactional
    fun issueJwts(member: Member): Array<String> {
        // 마지막 로그인 시간 업데이트
        member.updateLastSignIn()

        // refresh token을 발급한다.
        val newRefreshToken: String = JWT.create()
            .withIssuer(issuer)
            .withSubject("refreshToken")
            .withIssuedAt(Date(System.currentTimeMillis()))
            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenExpiration))
            .withClaim("memberId", member.id.toString())
            .withClaim("issuedTime", System.currentTimeMillis())
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
            .withIssuedAt(Date(System.currentTimeMillis()))
            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenExpiration))
            .withClaim("memberId", member.id.toString())
            .withClaim("issuedTime", System.currentTimeMillis())
            .sign(HMAC512(secret))

        return arrayOf(newAccessToken, newRefreshToken)
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
            .withIssuedAt(Date(System.currentTimeMillis()))
            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenExpiration))
            .withClaim("memberId", memberId.toString())
            .withClaim("issuedTime", System.currentTimeMillis())
            .sign(HMAC512(secret))

        // Refresh Token Entity 업데이트
        member.refreshToken!!.updateToken(newRefreshToken)

        // access token을 발급한다.
        val newAccessToken: String = JWT.create()
            .withIssuer(issuer)
            .withSubject("accessToken")
            .withIssuedAt(Date(System.currentTimeMillis()))
            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenExpiration))
            .withClaim("memberId", memberId.toString())
            .withClaim("issuedTime", System.currentTimeMillis())
            .sign(HMAC512(secret))

        return arrayOf(newAccessToken, newRefreshToken)
    }

//    @Transactional(noRollbackFor = [JWTVerificationException::class, NoSuchMemberException::class]) // JWTVerificationException 발생해도 롤백 X
//    fun reissueJwts(memberId: Long?, refreshToken: String): Array<String> {
////        , String deviceToken)
////        // RefreshToken과 함께 DeviceTokenEntity를 찾아
////        DeviceToken deviceTokenEntity = deviceTokenRepository.findByDeviceTokenWithRefreshToken(deviceToken)
////            // Device Token이 DB에 없으면, Device Token이 바뀐 상황
////            .orElseThrow(() -> {
////                // RefreshToken을 DB에서 삭제
////                refreshTokenRepository.deleteByRefreshToken(refreshToken);
////
////                Member member = memberRepository.findNotDeletedByEmailWithRefreshTokenAndSubscriptionAndDeviceToken(email)
////                    .orElseThrow(() -> new NoSuchMemberException(new StringBuffer().append(SUCH.getMessage()).append(MEMBER.getMessage()).append(NOT_EXISTS.getMessage()).toString()));
////
////                // 만료된 Device Token으로 구독한 모든 Topic을 구독 해제한다.
//////                subscriptionService.unsubscribeFromAllTopics(deviceToken, member);
////
////                throw new NoSuchDeviceTokenException(new StringBuffer().append(SUCH.getMessage()).append(DEVICE_TOKEN.getMessage()).append(NOT_EXISTS.getMessage()).toString());
////            });
////
////        // 일치하는 Device Token이 있으면
////        // 그 RefreshToken이 들어온 RefreshToken과 일치하는지 확인
////        // 유저가 보낸 Refresh Token와 다르면, 유효하지 않은 Refresh Token이므로
////        RefreshToken refreshTokenEntity = Optional.ofNullable(deviceTokenEntity.getRefreshToken())
////            // RefreshToken이 DB에 없으면
////            .orElseThrow(() -> {
////                throw new JWTVerificationException(new StringBuffer().append(REFRESH_TOKEN.getMessage()).append(INVALID.getMessage()).toString());
////            });
//
//        // Refresh Token 조회
//
//        val key = REFRESH_TOKEN_REDIS_KEY + refreshToken
//        // Redis에서 RefreshToken 조회
//        val memberIdInRedis = redisTemplate!!.opsForValue()[key]
//            ?: // 꺼져
//            throw NoSuchRefreshTokenException()
//
//        // Redis에 RefreshToken이 없으면
//
//        // Redis에서 RefreshToken 삭제
//        redisTemplate.delete(key)
//
//        val member: Member = memberRepository.findNotDeletedById(memberId)
//            .orElseThrow { NoSuchMemberException() }
//
//        // refreshToken이 유효하면
//        // access token을 발급한다.
//        val newAccessToken: String = JWT.create()
//            .withIssuer(issuer)
//            .withSubject("accessToken")
//            .withIssuedAt(Date(System.currentTimeMillis()))
//            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenExpiration))
//            .withClaim("memberId", java.lang.String.valueOf(member.getId()))
//            .withClaim("issuedTime", System.currentTimeMillis())
//            .sign(HMAC512(secret))
//
//        // refresh token을 발급한다.
//        val newRefreshToken: String = JWT.create()
//            .withIssuer(issuer)
//            .withSubject("refreshToken")
//            .withIssuedAt(Date(System.currentTimeMillis()))
//            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenExpiration))
//            .withClaim("memberId", java.lang.String.valueOf(member.getId()))
//            .withClaim("issuedTime", System.currentTimeMillis())
//            .sign(HMAC512(secret))
//
//        return arrayOf(newAccessToken, newRefreshToken)
//    }
//
//    fun saveRefreshTokenToRedis(refreshToken: String, memberId: Long) {
//        val valueOperations = redisTemplate!!.opsForValue()
//        valueOperations[REFRESH_TOKEN_REDIS_KEY + refreshToken, memberId.toString(), refreshTokenExpiration] =
//            TimeUnit.SECONDS
//    }
//
//    fun deleteRefreshTokenFromRedis(refreshToken: String) {
//        redisTemplate!!.delete(REFRESH_TOKEN_REDIS_KEY + refreshToken)
//    }
//
//    // (RefreshToken, MemberId) 저장 TTL 1주일
//    // Auth:RefreshToken:{RefreshToken} -> MemberId
//    //    // refreshToken을 삭제한다.
//    //    // 검증 안하고 그냥 삭제해도 될 듯? - 해시된 비번들만 들어있으니까
//    //    @Transactional
//    //    public void deleteRefreshToken(String refreshToken) throws JWTVerificationException {
//    //        // refreshToken을 삭제한다.
//    //        refreshTokenRepository.deleteByRefreshToken(refreshToken);
//    //    }
    fun extractAccessToken(request: HttpServletRequest): String {
        val accessToken = request.getHeader(ACCESS_TOKEN_HEADER)

        if (accessToken.isNullOrBlank()) {
            throw InvalidAccessTokenException()
        }

        return accessToken.replace(BEARER, "")
    }

    fun extractRefreshToken(request: HttpServletRequest): String {
        val refreshToken = request.getHeader(REFRESH_TOKEN_HEADER)

        // refreshToken 값 검증
        if (refreshToken.isNullOrBlank()) {
            throw InvalidRefreshTokenException()
        }

        return refreshToken.replace(BEARER, "")
    }

    /**
     * 헤더에서 AccessToken 추출 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토큰만 가져오기 위해서 헤더를 가져온 후 "Bearer"를
     * 삭제(""로 replace)
     */
    fun validateAccessToken(accessToken: String): Boolean {
        try {
            JWT.require(HMAC512(secret))
                .withIssuer(issuer)
                .withSubject("accessToken")
                .build() // 반환된 빌더로 JWT verifier 생성
                .verify(accessToken) // accessToken을 검증하고 유효하지 않다면 예외 발생

            return true
        } catch (e: JWTVerificationException) {
            return false
        }
    }

    fun extractMemberIdFromAccessToken(accessToken: String): Long {
        try {
            // accessToken 값 검증
            val jwt: DecodedJWT = JWT.require(HMAC512(secret))
                .withIssuer(issuer)
                .withSubject("accessToken")
                .build() // 반환된 빌더로 JWT verifier 생성
                .verify(accessToken) // accessToken을 검증하고 유효하지 않다면 예외 발생

            return jwt.getClaim("memberId")
                .asString().toLong() // claim(MemberId) 가져오기
        } catch (e: JWTVerificationException) {
            throw InvalidAccessTokenException()
        }
    }

    /**
     * 헤더에서 RefreshToken 추출 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토큰만 가져오기 위해서 헤더를 가져온 후 "Bearer"를
     * 삭제(""로 replace)
     */
    fun validateAndExtractMemberIdFromRefreshToken(refreshToken: String): Long {
        try {
            // refreshToken을 검증한다.
            val memberIdInRefreshToken: Long = JWT.require(HMAC512(secret))
                .withIssuer(issuer)
                .withSubject("refreshToken")
                .build()
                .verify(refreshToken)
                .getClaim("memberId")
                .asString().toLong()

            return memberIdInRefreshToken
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
