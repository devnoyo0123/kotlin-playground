package com.example.bookorder

import com.example.bookorder.distributed_lock.DistributedLockPort
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit


@Component
class RedissonLockAdapter(private val redissonClient: RedissonClient) : DistributedLockPort {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private fun generateKey(key: String): String = "lock:$key"

    override fun acquireMultiLock(keys: List<String>, lockTime: Long, leaseTime: Long): Boolean {
        logger.info("Attempting to acquire multiple locks for keys: {}", keys)
        // 동일한 키에 대한 중복 락 방지
        val locks = keys.distinct().map { redissonClient.getLock(generateKey(it)) }

        // 중복된 키 제거 후 멀티락 생성
        val multiLock = redissonClient.getMultiLock(*locks.toTypedArray())

        return try {
            val acquired = multiLock.tryLock(lockTime, leaseTime, TimeUnit.MILLISECONDS)
            if (acquired) {
                logger.info("Multiple locks acquired for keys: {}", keys)
            } else {
                logger.warn("Failed to acquire multiple locks for keys: {}", keys)
            }
            acquired
        } catch (e: InterruptedException) {
            logger.error("Interrupted while trying to acquire multiple locks for keys: {}", keys, e)
            false
        } catch (e: Exception) {
            logger.error("Error while trying to acquire multiple locks for keys: {}", keys, e)
            false
        }
    }

    override fun releaseMultiLock(keys: List<String>) {
        logger.info("Attempting to release multiple locks for keys: {}", keys)
        try {
            val locks = keys.distinct().map { redissonClient.getLock(generateKey(it)) }
            val multiLock = redissonClient.getMultiLock(*locks.toTypedArray())
            multiLock.unlock()
            logger.info("Multiple locks released successfully for keys: {}", keys)
        } catch (e: Exception) {
            logger.error("Error while releasing multiple locks for keys: {}", keys, e)
        }
    }
}