package com.example.bookorder.create

data class CreateOrderRequest(
    val idempotencyKey: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val bookId: Long,
    val quantity: Int
)