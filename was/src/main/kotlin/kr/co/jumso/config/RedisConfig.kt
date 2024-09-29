package kr.co.jumso.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
class RedisConfig {
    @Value("\${spring.redis.host}")
    private lateinit var host: String

    @Value("\${spring.redis.port}")
    private lateinit var port: String

    @Value("\${spring.redis.username}")
    private lateinit var redisUsername: String

    @Value("\${spring.redis.password}")
    private lateinit var redisPassword: String

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisConfiguration = RedisStandaloneConfiguration()
        redisConfiguration.hostName = host
        redisConfiguration.port = port.toInt()
        redisConfiguration.username = redisUsername
        redisConfiguration.password = RedisPassword.of(redisPassword)

        val lettuceConnectionFactory = LettuceConnectionFactory(redisConfiguration)
        return lettuceConnectionFactory
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = redisConnectionFactory()

        // 모든 경우
        redisTemplate.setDefaultSerializer(StringRedisSerializer())

        // Transaction
        redisTemplate.setEnableTransactionSupport(true)

        return redisTemplate
    }
}
