package com.example.bookorder

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(RedisSingleProperties::class)
class RedissonConfig(val singleProperty: RedisSingleProperties) {

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        val serversConfig = config.useSingleServer()

        serversConfig
            .setAddress("redis://${singleProperty.node}")
            .setConnectTimeout(singleProperty.connectTimeout)
            .setRetryAttempts(singleProperty.retryAttempts)

        return Redisson.create(config)
    }
}