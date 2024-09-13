package com.example.simple_blog.config

import com.littlenb.snowflake.sequence.IdGenerator
import com.littlenb.snowflake.support.ElasticIdGeneratorFactory
import com.littlenb.snowflake.worker.SimpleWorkerIdAssigner
import com.littlenb.snowflake.worker.WorkerIdAssigner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit.MILLISECONDS


@Configuration
class TwitterSnowflakeConfig {
    @Bean
    fun snowflake(): IdGenerator {
        val elasticFactory = ElasticIdGeneratorFactory()

        // TimeBits + WorkerBits + SeqBits = 64 -1
        elasticFactory.setTimeBits(41)
        elasticFactory.setWorkerBits(10)
        elasticFactory.setSeqBits(12)

        // 시간
        elasticFactory.setTimeUnit(MILLISECONDS)

        elasticFactory.setEpochTimestamp(1483200000000L)

        val workerIdAssigner: WorkerIdAssigner = SimpleWorkerIdAssigner(currentTimeMillis())

        return elasticFactory.create(workerIdAssigner)
    }
}