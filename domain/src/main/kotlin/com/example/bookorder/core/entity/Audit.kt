package com.example.bookorder.core.entity

import java.time.LocalDateTime

open class Audit(
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var deletedAt: LocalDateTime? = null
)