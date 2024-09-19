package com.example.jumso.config

import com.littlenb.snowflake.sequence.IdGenerator
import com.littlenb.snowflake.support.ElasticIdGeneratorFactory
import com.littlenb.snowflake.worker.SimpleWorkerIdAssigner
import com.littlenb.snowflake.worker.WorkerIdAssigner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.LocalDateTime.of
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit.MILLISECONDS


@Configuration
class TwitterSnowflakeConfig {
    @Bean
    fun idGenerator(): IdGenerator {
        val elasticFactory = ElasticIdGeneratorFactory()

        // TimeBits + WorkerBits + SeqBits = 64 -1
        elasticFactory.setTimeBits(41)
        elasticFactory.setWorkerBits(10)
        elasticFactory.setSeqBits(12)

        // 시간
        elasticFactory.setTimeUnit(MILLISECONDS)

        // 2024년 09월 13일 00시 설정
        val localDateTime: LocalDateTime = of(2024, 9, 13, 0, 0)

        // 한국 표준시(GMT+9)로 변환
        val zonedDateTime: ZonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Seoul"))

        elasticFactory.setEpochTimestamp(zonedDateTime.toInstant().toEpochMilli())

        val workerIdAssigner: WorkerIdAssigner = SimpleWorkerIdAssigner(0)

        return elasticFactory.create(workerIdAssigner)
    }
}
