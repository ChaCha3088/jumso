package kr.co.jumso.domain.member.service

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.jumso.domain.auth.dto.ResetPasswordRequest
import kr.co.jumso.domain.kafka.dto.KafkaEmailRequest
import kr.co.jumso.domain.kafka.enumstorage.EmailType.RESET_PASSWORD
import kr.co.jumso.domain.kafka.enumstorage.KafkaEmail.KAFKA_EMAIL_SERVER
import kr.co.jumso.domain.member.dto.request.UpdateIntroductionRequest
import kr.co.jumso.domain.member.dto.request.UpdateLocationRequest
import kr.co.jumso.domain.member.exception.NoSuchMemberException
import kr.co.jumso.domain.member.repository.MemberRepository
import kr.co.jumso.util.PasswordValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID.randomUUID

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,

    private val kafkaTemplate: KafkaTemplate<String, String>,

    private val passwordValidator: PasswordValidator,
    private val objectMapper: ObjectMapper,
) {
    @Value("\${spring.kafka.email-server-port}")
    private lateinit var emailServerPort: String

    @Transactional
    fun requestResetPassword(email: String) {
        val member = memberRepository.findNotDeletedByEmail(email.trim())
            ?: throw NoSuchMemberException()

        val split = member.email.split("@")
        val username = split[0]
        val domain = split[1]

        val newVerificationCode = member.requestResetPassword()

        memberRepository.save(member)

        // Kafka로 비밀번호 초기화 이메일 발송
        kafkaTemplate.send(
            "$KAFKA_EMAIL_SERVER-$emailServerPort",
            objectMapper.writeValueAsString(
                KafkaEmailRequest(
                    emailType = RESET_PASSWORD,
                    memberUsername = username,
                    domain = domain,
                    verificationCode = newVerificationCode,
                )
            )
        )
    }

    @Transactional
    fun resetPassword(resetPasswordRequest: ResetPasswordRequest) {
        val member = memberRepository.findNotDeletedByVerificationCode(resetPasswordRequest.verificationCode.trim())
            ?: throw NoSuchMemberException()

        // 비밀번호 규칙 검증
        val validatedAndEncodedPassword = passwordValidator.validate(
            member.email.split("@")[0],
            resetPasswordRequest.newPassword,
            resetPasswordRequest.newPasswordConfirm,
        )

        member.resetPassword(validatedAndEncodedPassword)

        memberRepository.save(member)
    }

    @Transactional
    fun updateLocation(memberId: Long, updateLocationRequest: UpdateLocationRequest) {
        val member = memberRepository.findNotDeletedById(memberId)
            ?: throw NoSuchMemberException()

        member.updateLocation(updateLocationRequest.latitude!!, updateLocationRequest.longitude!!)

        memberRepository.save(member)
    }

    @Transactional
    fun updateIntroduce(memberId: Long, updateIntroductionRequest: UpdateIntroductionRequest) {
        val member = memberRepository.findNotDeletedById(memberId)
            ?: throw NoSuchMemberException()

        member.updateIntroduction(updateIntroductionRequest.introduction!!)

        memberRepository.save(member)
    }
}
