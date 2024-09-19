package com.example.jumso.domain.member.controller

import com.example.jumso.domain.auth.annotation.MemberId
import com.example.jumso.domain.member.dto.UpdateIntroductionRequest
import com.example.jumso.domain.member.dto.UpdateLocationRequest
import com.example.jumso.domain.member.exception.NoSuchMemberException
import com.example.jumso.domain.member.service.MemberService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/members"], consumes = [APPLICATION_JSON_VALUE])
class MemberController(
    private val memberService: MemberService
) {
    @PostMapping("/location")
    fun updateLocation(
        @MemberId memberId: Long,
        @Validated @RequestBody updateLocationRequest: UpdateLocationRequest
    ) {
        memberService.updateLocation(memberId, updateLocationRequest)
    }

    @PostMapping("/introduce")
    fun updateIntroduce(
        @MemberId memberId: Long,
        @Validated @RequestBody updateIntroduceRequest: UpdateIntroductionRequest
    ) {
        memberService.updateIntroduce(memberId, updateIntroduceRequest)
    }

    @ExceptionHandler(NoSuchMemberException::class)
    fun handleException(e: NoSuchMemberException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(e.message)
    }
}
