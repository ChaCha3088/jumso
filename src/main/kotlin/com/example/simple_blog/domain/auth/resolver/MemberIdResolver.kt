package com.example.simple_blog.domain.auth.resolver

import com.example.simple_blog.domain.auth.annotation.MemberId
import com.example.simple_blog.domain.auth.service.JwtService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class MemberIdResolver(
    private val jwtService: JwtService
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == Long::class.java && parameter.hasParameterAnnotation(MemberId::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter, mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.nativeRequest as HttpServletRequest
        val accessToken: String = jwtService.extractAccessToken(request)
        jwtService.validateAccessToken(accessToken)

        return jwtService.extractMemberIdFromAccessToken(accessToken)
    }
}
