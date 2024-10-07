package kr.co.jumso.domain.member.controller

import kr.co.jumso.domain.member.annotation.MemberId
import kr.co.jumso.domain.member.dto.request.UpdateIntroductionRequest
import kr.co.jumso.domain.member.dto.request.UpdateLocationRequest
import kr.co.jumso.domain.member.exception.NoSuchMemberException
import kr.co.jumso.domain.member.service.MemberService
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
