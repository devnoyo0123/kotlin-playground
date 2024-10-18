package com.example.bookorder.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry
import org.springframework.retry.backoff.BackOffContext
import org.springframework.retry.backoff.BackOffPolicy
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

@Configuration
@EnableRetry
class RetryConfig {

    @Bean
    fun customBackOffPolicy(): BackOffPolicy {
        return ExponentialBackoffWithJitterPolicy(
            initialIntervalMillis = 100,
            multiplier = 2.0,
            maxIntervalMillis = 30000,
            minJitterMillis = 0,
            maxJitterMillis = 100
        )
    }
}

class ExponentialBackoffWithJitterPolicy(
    private val initialIntervalMillis: Long = 100,
    private val multiplier: Double = 2.0,
    private val maxIntervalMillis: Long = 30000, 
    private val minJitterMillis: Long = 0,
    private val maxJitterMillis: Long = 100
) : BackOffPolicy {

    override fun start(context: org.springframework.retry.RetryContext?): BackOffContext {
        return ExponentialBackoffContext()
    }

    override fun backOff(backOffContext: BackOffContext?) {
        if (backOffContext !is ExponentialBackoffContext) {
            throw IllegalArgumentException("BackOffContext must be an ExponentialBackoffContext")
        }

        val nextBackoff = calculateNextBackoff(backOffContext.attempt)
        backOffContext.attempt++

        try {
            Thread.sleep(nextBackoff)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    private fun calculateNextBackoff(attempt: Int): Long {
        val interval = min(
            maxIntervalMillis.toDouble(),
            initialIntervalMillis * multiplier.pow(attempt.toDouble())
        ).toLong()

        val jitter = Random.nextLong(minJitterMillis, maxJitterMillis + 1)
        return interval + jitter
    }

    private inner class ExponentialBackoffContext : BackOffContext {
        var attempt: Int = 0
    }
}