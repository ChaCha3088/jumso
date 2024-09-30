package kr.co.jumso.domain.member.service

import kr.co.jumso.domain.auth.exception.CompanyEmailNotFoundException
import kr.co.jumso.domain.auth.repository.CompanyEmailRepository
import kr.co.jumso.domain.member.dto.TemporaryMemberRequest
import kr.co.jumso.domain.member.entity.TemporaryMember
import kr.co.jumso.domain.member.exception.InvalidVerificationCodeException
import kr.co.jumso.domain.member.exception.MemberExistsException
import kr.co.jumso.domain.member.exception.TemporaryMemberExistsException
import kr.co.jumso.domain.member.repository.MemberRepository
import kr.co.jumso.domain.member.repository.TemporaryMemberRepository
import kr.co.jumso.util.PasswordValidator
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
        // ToDo: Kafka에 이메일 전송 요청을 보내고, Kafka Consumer가 이메일을 보내도록 구현
        // memberUsername: String, domain: String, verificationCode: String
    }

    @Transactional
    fun verify(verificationCode: String) {
        val temporaryMember: TemporaryMember = temporaryMemberRepository.findByVerificationCode(verificationCode)
            ?: throw InvalidVerificationCodeException()

        // temporaryMember 인증
        temporaryMember.verify(verificationCode)

        temporaryMemberRepository.save(temporaryMember)
    }
}
