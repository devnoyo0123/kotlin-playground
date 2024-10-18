package com.example.bookorder.distributed_lock

interface DistributedLockPort {
    fun acquireMultiLock(keys: List<String>, lockTime: Long, leaseTime: Long): Boolean
    fun releaseMultiLock(keys: List<String>)
}