package kr.co.jumso.domain.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.co.jumso.domain.member.annotation.TemporaryMemberId
import kr.co.jumso.domain.auth.dto.SignInRequest
import kr.co.jumso.domain.auth.exception.CompanyEmailNotFoundException
import kr.co.jumso.domain.auth.exception.InvalidAccessTokenException
import kr.co.jumso.domain.auth.exception.InvalidPasswordException
import kr.co.jumso.domain.auth.exception.InvalidRefreshTokenException
import kr.co.jumso.domain.auth.service.AuthService
import kr.co.jumso.domain.member.dto.request.EnrollRequest
import kr.co.jumso.domain.member.dto.response.MemberResponse
import kr.co.jumso.domain.member.dto.request.TemporaryMemberRequest
import kr.co.jumso.domain.member.exception.*
import kr.co.jumso.domain.member.service.TemporaryMemberService
import org.apache.tomcat.websocket.Constants.UNAUTHORIZED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/auth"], consumes = [APPLICATION_JSON_VALUE])
class AuthController(
    private val authService: AuthService,
    private val temporaryMemberService: TemporaryMemberService,

    private val objectMapper: ObjectMapper,
) {
    @PostMapping("/signin")
    fun signIn(
        @Validated @RequestBody signInRequest: SignInRequest,
        response: HttpServletResponse
    ): ResponseEntity<MemberResponse> {
        return ResponseEntity<MemberResponse>(
            authService.signIn(signInRequest, response),
            OK
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
    fun create(
        @Validated @RequestBody temporaryMemberRequest: TemporaryMemberRequest,
        response: HttpServletResponse
    ) {
        temporaryMemberService.create(
            temporaryMemberRequest,
            response
        )
    }

    @PostMapping("/temporary-signin")
    fun temporarySignIn(
        @Validated @RequestBody signInRequest: SignInRequest,
        response: HttpServletResponse,
    ) {
        authService.temporarySignIn(signInRequest, response)
    }

    @GetMapping("/verify")
    fun verify(
        @TemporaryMemberId temporaryMemberId: Long,
        @RequestParam(value = "verificationCode", required = true) verificationCode: String,
    ): ResponseEntity<String> {
        temporaryMemberService.verify(temporaryMemberId, verificationCode.trim())

        return ResponseEntity.ok().build()
    }

    @PostMapping("/enroll")
    fun enroll(
        @TemporaryMemberId temporaryMemberId: Long,
        @Validated @RequestBody enrollRequest: EnrollRequest,
        response: HttpServletResponse,
    ): ResponseEntity<String> {
        val memberResponse = temporaryMemberService.enroll(temporaryMemberId, enrollRequest, response)

        return ResponseEntity.ok().body(objectMapper.writeValueAsString(memberResponse))
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
