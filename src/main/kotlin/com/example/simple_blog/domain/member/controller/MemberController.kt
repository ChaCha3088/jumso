package com.example.simple_blog.domain.member.controller

import com.example.simple_blog.domain.auth.annotation.MemberId
import com.example.simple_blog.domain.member.dto.UpdateLocationRequest
import com.example.simple_blog.domain.member.service.MemberService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/members"], consumes = [APPLICATION_JSON_VALUE])
class MemberController(
    private val memberService: MemberService
) {
    @PostMapping
    fun updateLocation(
        @MemberId memberId: Long,
        @Validated @RequestBody updateLocationRequest: UpdateLocationRequest
    ) {
        memberService.updateLocation(memberId, updateLocationRequest)
    }
}
