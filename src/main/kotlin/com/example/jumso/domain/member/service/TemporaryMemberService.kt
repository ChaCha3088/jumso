package com.example.jumso.domain.member.service

import com.example.jumso.domain.auth.exception.CompanyEmailNotFoundException
import com.example.jumso.domain.auth.repository.CompanyEmailRepository
import com.example.jumso.domain.member.dto.TemporaryMemberRequest
import com.example.jumso.domain.member.entity.TemporaryMember
import com.example.jumso.domain.member.exception.InvalidVerificationCodeException
import com.example.jumso.domain.member.exception.MemberExistsException
import com.example.jumso.domain.member.exception.TemporaryMemberExistsException
import com.example.jumso.domain.member.repository.MemberRepository
import com.example.jumso.domain.member.repository.TemporaryMemberRepository
import com.example.jumso.util.PasswordValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TemporaryMemberService(
    private val temporaryMemberRepository: TemporaryMemberRepository,
    private val memberRepository: MemberRepository,
    private val companyEmailRepository: CompanyEmailRepository,

    private val passwordEncoder: PasswordEncoder,
    private val passwordValidator: PasswordValidator,

    private val javaMailSender: JavaMailSender,

    @Value("\${spring.mail.username}")
    private val username: String,
) {
    @Transactional
    fun create(temporaryMemberRequest: TemporaryMemberRequest) {
        // password 검증
        passwordValidator.validate(temporaryMemberRequest.username!!, temporaryMemberRequest.password!!)

        // 이미 같은 회사에 같은 username이 존재하는지 확인
        if (temporaryMemberRepository.existsByUsernameAndCompanyEmailId(temporaryMemberRequest.username!!, temporaryMemberRequest.companyEmailId!!)) {
            throw TemporaryMemberExistsException()
        }

        // companyEmailId로 companyEmail을 조회하고
        val companyEmail = companyEmailRepository.findById(temporaryMemberRequest.companyEmailId!!)
            .orElseThrow { throw CompanyEmailNotFoundException() }

        // username + @ + companyEmail.address로 이미 존재하는 회원인지 확인
        if (memberRepository.existsByEmail("${temporaryMemberRequest.username}@${companyEmail.address}")) {
            throw MemberExistsException()
        }

        val newTemporaryMember = temporaryMemberRepository.save(temporaryMemberRequest.toEntity(passwordEncoder))

        // 이메일 보내기
        sendEmail(newTemporaryMember.username, companyEmail.address, newTemporaryMember.verificationCode)
    }

    @Transactional
    fun verify(verificationCode: String) {
        val temporaryMember: TemporaryMember = temporaryMemberRepository.findByVerificationCode(verificationCode)
            ?: throw InvalidVerificationCodeException()

        // Member로 옮기기
        val newMember = temporaryMember.verify(verificationCode)

        // temporaryMember 삭제
        temporaryMemberRepository.delete(temporaryMember)

        // Member 저장
        memberRepository.save(newMember)
    }

    private fun sendEmail(username: String, domain: String, verificationCode: String) {
        SimpleMailMessage().apply {
            from = this@TemporaryMemberService.username
            setTo("$username@$domain")
            subject = "점소 회원가입 인증 이메일입니다."
            // https://localhost:9090/api/auth/verify?verificationCode=${verificationCode}로 링크 버튼
            text = "아래 코드를 입력하여 회원가입을 완료해주세요.\n" +
                verificationCode
        }.let { javaMailSender.send(it) }
    }
}
