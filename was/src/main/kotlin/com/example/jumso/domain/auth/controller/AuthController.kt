package com.example.jumso.domain.auth.controller

import com.example.jumso.domain.auth.dto.SignInRequest
import com.example.jumso.domain.auth.exception.CompanyEmailNotFoundException
import com.example.jumso.domain.auth.exception.InvalidAccessTokenException
import com.example.jumso.domain.auth.exception.InvalidPasswordException
import com.example.jumso.domain.auth.exception.InvalidRefreshTokenException
import com.example.jumso.domain.auth.service.AuthService
import com.example.jumso.domain.member.dto.TemporaryMemberRequest
import com.example.jumso.domain.member.dto.MemberResponse
import com.example.jumso.domain.member.exception.*
import com.example.jumso.domain.member.service.MemberService
import com.example.jumso.domain.member.service.TemporaryMemberService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.tomcat.websocket.Constants.UNAUTHORIZED
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.ALL_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod.GET

@RestController
@RequestMapping(value = ["/api/auth"], consumes = [APPLICATION_JSON_VALUE])
class AuthController(
    private val authService: AuthService,
    private val temporaryMemberService: TemporaryMemberService,
    private val memberService: MemberService,
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
    fun create(@Validated @RequestBody temporaryMemberRequest: TemporaryMemberRequest) {
        temporaryMemberService.create(temporaryMemberRequest)
    }

    @RequestMapping(method = [GET], value = ["/verify"], consumes = [ALL_VALUE])
    fun verify(
        @RequestParam(value = "verificationCode", required = true) verificationCode: String
    ): ResponseEntity<String> {
        temporaryMemberService.verify(verificationCode)

        return ResponseEntity.ok().build()
    }

    @ExceptionHandler(PasswordInvalidException::class)
    fun handleException(e: PasswordInvalidException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(e.message)
    }

    @ExceptionHandler(CompanyEmailNotFoundException::class)
    fun handleException(e: CompanyEmailNotFoundException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(e.message)
    }

    @ExceptionHandler(MemberExistsException::class)
    fun handleException(e: MemberExistsException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(e.message)
    }

    @ExceptionHandler(TemporaryMemberExistsException::class)
    fun handleException(e: TemporaryMemberExistsException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(e.message)
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

    @ExceptionHandler(InvalidVerificationCodeException::class)
    fun handleException(e: InvalidVerificationCodeException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(e.message)
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
