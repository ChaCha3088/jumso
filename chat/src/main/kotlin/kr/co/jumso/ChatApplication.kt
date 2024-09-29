package kr.co.jumso

import kr.co.jumso.domain.chat.enumstorage.RedisKeys.CHAT_SERVER_LOAD
import kr.co.jumso.domain.chat.enumstorage.RedisKeys.MEMBER_ID_TO_SERVER_PORT
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@SpringBootApplication
class ChatApplication

fun main(args: Array<String>) {
	runApplication<ChatApplication>(*args)
}

@Component
class RedisInit(
    @Value("\${server.port}")
    private val serverPort: String,

    private val redisTemplate: RedisTemplate<String, Any>,
): ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        try {
            redisTemplate.opsForHash<String, String>()
                .delete(MEMBER_ID_TO_SERVER_PORT.toString())
        } catch (_: Exception) {

        }

        redisTemplate.opsForZSet().remove(CHAT_SERVER_LOAD.toString(), serverPort)
    }
}
