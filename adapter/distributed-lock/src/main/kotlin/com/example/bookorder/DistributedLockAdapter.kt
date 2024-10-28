package com.example.bookorder

import com.example.bookorder.distriubted_lock.DistributedLockPort
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class DistributedLockAdapter(val redissonClient: RedissonClient) : DistributedLockPort {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun generateKey(key: String): String {
        return "lock:$key"
    }

    override fun multiLock(keys: List<String>, lockTime: Long, leaseTime: Long, timeUnit: TimeUnit): Boolean {
        logger.info("Attempting to acquire multiple locks for keys: {}", keys)
        // 동일한 키에 대한 중복 락 방지
        val locks = keys.distinct().map { redissonClient.getLock(generateKey(it)) }

        // 중복된 키 제거 후 멀티락 생성
        val multiLock = redissonClient.getMultiLock(*locks.toTypedArray())

        return try {
            val acquired = multiLock.tryLock(lockTime, leaseTime, timeUnit)
            if (acquired) {
                logger.info("Multiple locks acquired for keys: {}", keys)
                true
            } else {
                logger.warn("Failed to acquire multiple locks for keys: {}", keys)
                false
            }
        } catch (e: InterruptedException) {
            logger.error("Interrupted while trying to acquire multiple locks for keys: {}", keys, e)
            false
        } catch (e: Exception) {
            logger.error("Error while trying to acquire multiple locks for keys: {}", keys, e)
            false
        }
    }

    override fun multiUnlock(keys: List<String>) {
        logger.info("Attempting to release multiple locks")
        try {
            val locks = keys.distinct().map { redissonClient.getLock(generateKey(it)) }
            val multiLock = redissonClient.getMultiLock(*locks.toTypedArray())
            multiLock.unlock()
            logger.info("Multiple locks released successfully")
        } catch (e: Exception) {
            logger.error("Error while releasing multiple locks", e)
        }
    }
}