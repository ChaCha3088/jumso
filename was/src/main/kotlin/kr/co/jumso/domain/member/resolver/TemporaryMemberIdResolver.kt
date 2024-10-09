package kr.co.jumso.domain.member.resolver

import kr.co.jumso.domain.auth.service.JwtService
import jakarta.servlet.http.HttpServletRequest
import kr.co.jumso.domain.member.annotation.TemporaryMemberId
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class TemporaryMemberIdResolver(
    private val jwtService: JwtService,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == Long::class.java && parameter.hasParameterAnnotation(
            TemporaryMemberId::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter, mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.nativeRequest as HttpServletRequest
        val accessToken: String = jwtService.extractAccessTokenFromHeader(request)

        // memberId를 반환
        return jwtService.validateAndExtractTemporaryMemberIdFromAccessToken(accessToken)
    }
}
