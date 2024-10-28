package kr.co.jumso.domain.email.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.jumso.annotation.Valid
import kr.co.jumso.domain.chat.dto.KafkaMessage
import kr.co.jumso.domain.chat.enumstorage.MessageType.EMAIL
import kr.co.jumso.domain.email.service.EmailService
import kr.co.jumso.domain.kafka.dto.KafkaEmailRequest
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class EmailKafkaConsumer(
    private val emailService: EmailService,

    private val objectMapper: ObjectMapper,
) {
    // Kafka에서 들어오는 메시지를 처리
    @KafkaListener(topics = ["\${spring.kafka.consumer.topic}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun listen(message: String) {
        // json 파싱
        val chatMessageObject = objectMapper.readValue(message, KafkaMessage::class.java)

        processKafkaMessage(chatMessageObject)
    }

    // 서버가 이메일 요청 메시지를 수신할 때
    private fun processKafkaMessage(@Valid kafkaMessage: KafkaMessage) {
        when (kafkaMessage.type) {
            EMAIL -> {
                val kafkaEmailRequest = objectMapper.readValue(kafkaMessage.data.toString(), KafkaEmailRequest::class.java)

                // 이메일 요청 처리
                processEmailRequest(kafkaEmailRequest)
            }
            else -> {}
        }
    }

    private fun processEmailRequest(@Valid kafkaEmailRequest: KafkaEmailRequest) {
        emailService.sendEmail(
            memberUsername = kafkaEmailRequest.memberUsername,
            domain = kafkaEmailRequest.domain,
            verificationCode = kafkaEmailRequest.verificationCode
        )
    }
}
