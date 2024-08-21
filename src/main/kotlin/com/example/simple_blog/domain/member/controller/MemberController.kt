package com.example.simple_blog.domain.member.controller

import com.example.simple_blog.domain.member.dto.MemberRequest
import com.example.simple_blog.domain.member.service.MemberService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/members"], consumes = [APPLICATION_JSON_VALUE])
class MemberController(
    private val memberService: MemberService
) {
    @GetMapping
    fun findAll() = memberService.findAll()

    @PostMapping
    fun create(@Validated @RequestBody memberRequest: MemberRequest) {
        memberService.create(memberRequest)
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
