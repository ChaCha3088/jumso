package kr.co.jumso.dto.kafka

import kr.co.jumso.enumstorage.kafka.EmailType

data class KafkaEmailRequest(
    val emailType: EmailType,
    val memberUsername: String,
    val domain: String,
    val verificationCode: String
)
