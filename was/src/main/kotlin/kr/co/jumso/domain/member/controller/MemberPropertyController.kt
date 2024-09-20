package kr.co.jumso.domain.member.controller

import kr.co.jumso.domain.auth.annotation.MemberId
import kr.co.jumso.domain.member.dto.MemberPropertyRequest
import kr.co.jumso.domain.member.service.MemberPropertyService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.sql.SQLIntegrityConstraintViolationException

@RestController
@RequestMapping(value = ["/api/members/properties"], consumes = [APPLICATION_JSON_VALUE])
class MemberPropertyController(
    private val memberPropertyService: MemberPropertyService
) {
    @PostMapping
    fun create(
        @MemberId memberId: Long,
        @Validated @RequestBody memberPropertyRequest: MemberPropertyRequest
    ) {
        memberPropertyService.create(memberId, memberPropertyRequest)
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException::class)
    fun handleException(e: SQLIntegrityConstraintViolationException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body("Invalid property id")
    }
}
