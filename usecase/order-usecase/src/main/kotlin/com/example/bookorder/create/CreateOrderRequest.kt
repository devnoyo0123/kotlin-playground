package com.example.bookorder.create

data class CreateOrderRequest(
    val idempotencyKey: String,
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val bookId: Long,
    val quantity: Int
)