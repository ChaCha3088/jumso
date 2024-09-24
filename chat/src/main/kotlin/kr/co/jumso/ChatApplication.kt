package kr.co.jumso

import kr.co.jumso.enumstorage.RedisKeys.CHAT_SERVER_LOAD
import kr.co.jumso.enumstorage.RedisKeys.MEMBER_ID_TO_SERVER_PORT_AND_SESSION_ID
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
                .delete(MEMBER_ID_TO_SERVER_PORT_AND_SESSION_ID.key)
        } catch (_: Exception) {

        }

        redisTemplate.opsForZSet().remove(CHAT_SERVER_LOAD.key, serverPort)
    }
}
