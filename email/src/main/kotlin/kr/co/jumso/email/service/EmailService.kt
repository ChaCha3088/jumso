package kr.co.jumso.email.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val javaMailSender: JavaMailSender
) {
    @Value("\${spring.mail.username}")
    private lateinit var username: String

    fun sendEmail(memberUsername: String, domain: String, verificationCode: String) {
        SimpleMailMessage().apply {
            from = username
            setTo("$memberUsername@$domain")
            subject = "점소 회원가입 인증 이메일입니다."
            text = "아래 코드를 입력하여 회원가입을 완료해주세요.\n" +
                verificationCode
        }.let { javaMailSender.send(it) }
    }
}
