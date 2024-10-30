package kr.co.jumso

import kr.co.jumso.enumstorage.chat.RedisKeys.CHAT_SERVER_LOAD
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
        // redis에 해당 서버 포트 세션 개수를 0으로 초기화
        redisTemplate.opsForZSet().add(CHAT_SERVER_LOAD.toString(), serverPort, 0.0)
    }
}
