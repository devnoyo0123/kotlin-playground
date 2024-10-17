package com.example.bookorder.distributed_lock

import org.redisson.api.RLock
import java.util.concurrent.TimeUnit

interface DistributedLockPort {
    fun generateKey(key: String): String
    fun multiLock(keys: List<String>, lockTime: Long, leaseTime: Long, timeUnit: TimeUnit): RLock?
    fun multiUnlock(lock: RLock)
}