package com.example.bookorder.distriubted_lock

import java.util.concurrent.TimeUnit

interface DistributedLockPort {
    fun generateKey(key: String): String
    fun multiLock(keys: List<String>, lockTime: Long, leaseTime: Long, timeUnit: TimeUnit): Boolean
    fun multiUnlock(keys: List<String>)
}