package com.example.bookorder

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Primary

@ConfigurationProperties(prefix = "redis.single")
@Primary
data class RedisSingleProperties(
    var node: String? = null,
    var connectTimeout: Int = 10000,
    var retryAttempts: Int = 3,
)