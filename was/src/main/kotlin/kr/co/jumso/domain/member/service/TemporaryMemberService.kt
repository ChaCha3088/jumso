package kr.co.jumso.domain.member.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import kr.co.jumso.domain.auth.exception.CompanyEmailNotFoundException
import kr.co.jumso.domain.auth.repository.CompanyEmailRepository
import kr.co.jumso.domain.auth.service.JwtService
import kr.co.jumso.domain.kafka.dto.KafkaEmailRequest
import kr.co.jumso.domain.kafka.enumstorage.KafkaEmail.KAFKA_EMAIL_SERVER
import kr.co.jumso.domain.member.dto.TemporaryMemberRequest
import kr.co.jumso.domain.member.entity.TemporaryMember
import kr.co.jumso.domain.member.exception.InvalidVerificationCodeException
import kr.co.jumso.domain.member.exception.MemberExistsException
import kr.co.jumso.domain.member.exception.TemporaryMemberExistsException
import kr.co.jumso.domain.member.repository.MemberRepository
import kr.co.jumso.domain.member.repository.TemporaryMemberRepository
import kr.co.jumso.util.PasswordValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TemporaryMemberService(
    private val jwtService: JwtService,

    private val temporaryMemberRepository: TemporaryMemberRepository,
    private val memberRepository: MemberRepository,
    private val companyEmailRepository: CompanyEmailRepository,

    private val kafkaTemplate: KafkaTemplate<String, String>,

    private val passwordEncoder: PasswordEncoder,
    private val passwordValidator: PasswordValidator,
    private val objectMapper: ObjectMapper,
) {
    @Value("\${spring.kafka.email-server-port}")
    private lateinit var emailServerPort: String

    @Transactional
    fun create(
        temporaryMemberRequest: TemporaryMemberRequest,
        response: HttpServletResponse
    ) {
        temporaryMemberRequest.username = temporaryMemberRequest.username.trim()
        temporaryMemberRequest.password = temporaryMemberRequest.password.trim()
        temporaryMemberRequest.nickname = temporaryMemberRequest.nickname.trim()

        // password 검증
        passwordValidator.validate(temporaryMemberRequest.username, temporaryMemberRequest.password)

        // companyEmail 가져오기
        val companyEmail = companyEmailRepository.findById(temporaryMemberRequest.companyEmailId)
            .orElseThrow { throw CompanyEmailNotFoundException() }

        // 이미 존재하는 temporaryMember인지 확인
        if (temporaryMemberRepository.existsByEmail("${temporaryMemberRequest.username}@${companyEmail.address}")) {
            throw TemporaryMemberExistsException()
        }

        // 이미 존재하는 member인지 확인
        if (memberRepository.existsByEmail("${temporaryMemberRequest.username}@${companyEmail.address}")) {
            throw MemberExistsException()
        }

        val newTemporaryMember = temporaryMemberRepository.save(temporaryMemberRequest.toTemporaryMember(
            companyEmail.address,
            passwordEncoder
        ))

        // 새로운 토큰을 발급한다.
        val jwts: Array<String> = jwtService.issueTemporaryMemberJwts(newTemporaryMember)

        // 토큰을 Header에 설정
        setHeader(response, jwts)

        // Kafka에 이메일 전송 요청을 보내고, Kafka Consumer가 이메일을 보낸다.
        kafkaTemplate.send(
            "$KAFKA_EMAIL_SERVER-$emailServerPort",
            objectMapper.writeValueAsString(
                KafkaEmailRequest(
                    memberUsername = temporaryMemberRequest.username,
                    domain = companyEmail.address,
                    verificationCode = newTemporaryMember.verificationCode
                )
            )
        )
    }

    @Transactional
    fun verify(
        temporaryMemberId: Long,
        verificationCode: String,
    ) {
        val temporaryMember: TemporaryMember = temporaryMemberRepository.findById(temporaryMemberId)
            .orElseThrow { throw InvalidVerificationCodeException() }

        // temporaryMember 인증
        temporaryMember.verify(verificationCode)

        temporaryMemberRepository.save(temporaryMember)
    }

    private fun setHeader(response: HttpServletResponse, jwts: Array<String>) {
        jwtService.setAccessTokenOnHeader(response, jwts[0])
        jwtService.setRefreshTokenOnHeader(response, jwts[1])
    }
}
