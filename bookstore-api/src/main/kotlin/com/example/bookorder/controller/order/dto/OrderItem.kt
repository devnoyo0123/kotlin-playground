package com.example.bookorder.controller.order.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderItemDto(
    val id: Long,
    val bookId: Long,
    val quantity: Int,
    val price: BigDecimal,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)