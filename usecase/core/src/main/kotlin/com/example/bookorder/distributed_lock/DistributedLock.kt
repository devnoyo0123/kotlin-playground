package com.example.bookorder.distributed_lock

import java.util.concurrent.TimeUnit

/**
 * Redisson Distributed Lock annotation
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    /**
     * 키
     * 키는 여러개가 될수 있으므로 배열을 사용합니다
     */
    val keys: Array<String>,

    /**
     * 락의 시간 단위
     */
    val timeUnit: TimeUnit = TimeUnit.SECONDS,

    /**
     * 락 획득을 위해 waitTime 만큼 대기합니다
     */
    val waitTime: Long = 10L,

    /**
     * 락을 획득한 이후 leaseTime 이 지나면 락을 해제합니다
     */
    val leaseTime: Long = 1L
)