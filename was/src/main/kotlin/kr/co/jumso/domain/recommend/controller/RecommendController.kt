package kr.co.jumso.domain.recommend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.jumso.domain.member.annotation.MemberId
import kr.co.jumso.domain.recommend.dto.RecommendRequest
import kr.co.jumso.domain.recommend.service.RecommendService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/recommends"], consumes = [APPLICATION_JSON_VALUE])
class RecommendController(
    val recommendService: RecommendService,

    val objectMapper: ObjectMapper,
) {
    @GetMapping
    fun getRecommendList(
        @MemberId memberId: Long,
        @Validated @RequestBody recommendRequest: RecommendRequest,
    ): ResponseEntity<String> {
        val result = recommendService.getRecommendList(
            memberId = memberId,
            requestPropertyIds = recommendRequest.propertyIds,
        )

        return ResponseEntity.ok(objectMapper.writeValueAsString(result))
    }
}
