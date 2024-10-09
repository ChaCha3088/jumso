package kr.co.jumso.domain.kafka.dto

import kr.co.jumso.domain.kafka.enumstorage.EmailType

data class KafkaEmailRequest(
    val emailType: EmailType,
    val memberUsername: String,
    val domain: String,
    val verificationCode: String
)
