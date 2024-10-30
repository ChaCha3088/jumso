package kr.co.jumso.member.controller

import kr.co.jumso.member.annotation.MemberId
import kr.co.jumso.dto.member.request.MemberPropertyRequest
import kr.co.jumso.member.service.MemberPropertyService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.sql.SQLIntegrityConstraintViolationException

@RestController
@RequestMapping(value = ["/api/members/properties"], consumes = [APPLICATION_JSON_VALUE])
class MemberPropertyController(
    private val memberPropertyService: MemberPropertyService,
) {
    @PutMapping
    fun putMemberProperties(
        @MemberId memberId: Long,
        @Validated @RequestBody memberPropertyRequest: MemberPropertyRequest
    ) {
        memberPropertyService.putProperties(memberId, memberPropertyRequest)
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException::class)
    fun handleException(e: SQLIntegrityConstraintViolationException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body("Invalid property id")
    }
}
