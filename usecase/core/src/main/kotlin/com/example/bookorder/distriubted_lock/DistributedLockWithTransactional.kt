package com.example.bookorder.distriubted_lock

import java.util.concurrent.TimeUnit

/**
 * Redisson Distributed Lock annotation
 * DistributedLockWithTransactional 은 트랜잭션과 함께 사용하는 분산 락 어노테이션입니다.
 * method명이 prefix로 분산락의 키로 사용됩니다.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLockWithTransactional(
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