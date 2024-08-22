package com.example.simple_blog.domain.auth.controller

import com.example.simple_blog.domain.auth.dto.SignInRequest
import com.example.simple_blog.domain.auth.exception.InvalidAccessTokenException
import com.example.simple_blog.domain.auth.exception.InvalidPasswordException
import com.example.simple_blog.domain.auth.exception.InvalidRefreshTokenException
import com.example.simple_blog.domain.auth.service.AuthService
import com.example.simple_blog.domain.member.dto.MemberRequest
import com.example.simple_blog.domain.member.dto.MemberResponse
import com.example.simple_blog.domain.member.exception.NoSuchMemberException
import com.example.simple_blog.domain.member.service.MemberService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.tomcat.websocket.Constants.UNAUTHORIZED
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException.Unauthorized

@RestController
@RequestMapping(value = ["/api/auth"], consumes = [APPLICATION_JSON_VALUE])
class AuthController(
    private val authService: AuthService,
    private val memberService: MemberService
) {
    @PostMapping("/signin")
    fun signIn(
        @Validated @RequestBody signInRequest: SignInRequest,
        response: HttpServletResponse
    ): ResponseEntity<MemberResponse> {
        return ResponseEntity<MemberResponse>(
            authService.signIn(signInRequest, response),
            HttpStatus.OK
        )
    }

    @PostMapping("/reissue")
    fun reissue(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        authService.reissue(request, response)
    }

    @PostMapping("/signout")
    fun signOut(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> {
        authService.signOut(request, response)

        return ResponseEntity.ok().build()
    }

    @PostMapping("/signup")
    fun create(@Validated @RequestBody memberRequest: MemberRequest) {
        memberService.create(memberRequest)
    }

    @ExceptionHandler(NoSuchMemberException::class)
    fun handleException(e: NoSuchMemberException): ResponseEntity<String> {
        return ResponseEntity.status(UNAUTHORIZED).body(e.message)
    }

    @ExceptionHandler(InvalidPasswordException::class)
    fun handleException(e: InvalidPasswordException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(e.message)
    }

    @ExceptionHandler(InvalidAccessTokenException::class)
    fun handleException(e: InvalidAccessTokenException): ResponseEntity<String> {
        return ResponseEntity.status(UNAUTHORIZED).body(e.message)
    }

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleException(e: InvalidRefreshTokenException): ResponseEntity<String> {
        return ResponseEntity.status(UNAUTHORIZED).body(e.message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException(e: MethodArgumentNotValidException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(StringBuffer().apply {
            e.bindingResult.allErrors.forEach {
                append(it.defaultMessage).append("\n")
            }
        }.toString())
    }
}
