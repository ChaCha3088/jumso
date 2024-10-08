package kr.co.jumso.domain.member.service

import kr.co.jumso.domain.auth.dto.ResetPasswordRequest
import kr.co.jumso.domain.member.dto.request.UpdateIntroductionRequest
import kr.co.jumso.domain.member.dto.request.UpdateLocationRequest
import kr.co.jumso.domain.member.exception.NoSuchMemberException
import kr.co.jumso.domain.member.repository.MemberRepository
import kr.co.jumso.util.PasswordValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,

    private val passwordValidator: PasswordValidator,
) {
    @Transactional
    fun requestResetPassword(email: String) {
        val member = memberRepository.findNotDeletedByEmail(email.trim())
            ?: throw NoSuchMemberException()

        member.requestResetPassword()

        memberRepository.save(member)
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
