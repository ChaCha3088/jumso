package kr.co.jumso.domain.member.controller

import jakarta.validation.constraints.NotBlank
import kr.co.jumso.domain.member.annotation.MemberId
import kr.co.jumso.domain.member.dto.request.CompanyChangeRequest
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
    @PatchMapping("/request-change-company")
    fun requestChangeCompany(
        @MemberId memberId: Long,
        @Validated @RequestBody companyChangeRequest: CompanyChangeRequest,
    ) {
        memberService.requestChangeCompany(
            memberId = memberId,
            newCompanyId = companyChangeRequest.companyId,
            newDomain = companyChangeRequest.newDomain,
            newUsername = companyChangeRequest.newUsername,
        )
    }

    @PatchMapping("/change-company")
    fun changeCompany(
        @MemberId memberId: Long,
        @NotBlank @RequestParam(value = "verificationCode", required = true) verificationCode: String,
    ) {
        memberService.changeCompany(
            memberId,
            verificationCode,
        )
    }

    @PatchMapping("/location")
    fun updateLocation(
        @MemberId memberId: Long,
        @Validated @RequestBody updateLocationRequest: UpdateLocationRequest
    ) {
        memberService.updateLocation(memberId, updateLocationRequest)
    }

    @PatchMapping("/introduce")
    fun updateIntroduce(
        @MemberId memberId: Long,
        @Validated @RequestBody updateIntroduceRequest: UpdateIntroductionRequest
    ) {
        memberService.updateIntroduce(memberId, updateIntroduceRequest)
    }

    @DeleteMapping("/delete")
    fun deleteMember(
        @MemberId memberId: Long,
    ) {
        memberService.deleteMember(memberId)
    }

    @ExceptionHandler(NoSuchMemberException::class)
    fun handleException(e: NoSuchMemberException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(e.message)
    }
}
