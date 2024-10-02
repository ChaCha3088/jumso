package kr.co.jumso.domain.kafka.dto

data class KafkaEmailRequest(
    val memberUsername: String,
    val domain: String,
    val verificationCode: String
)
