package kr.co.jumso.member.resolver

import jakarta.servlet.http.HttpServletRequest
import kr.co.jumso.member.annotation.MemberId
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.lang.Long

class MemberIdResolver(
    private val jwtService: kr.co.jumso.auth.service.JwtService,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == Long::class.java && parameter.hasParameterAnnotation(
            MemberId::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter, mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.nativeRequest as HttpServletRequest
        val accessToken: String = jwtService.extractAccessTokenFromHeader(request)

        // memberId를 반환
        return jwtService.validateAndExtractMemberIdFromAccessToken(accessToken)
    }
}
