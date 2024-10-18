package com.example.bookorder.order

interface OrderPort {
    fun findById(id: OrderId): Order?
    fun save(order: Order): Order
    fun findByIdempotencyKey(idempotencyKey: String): Order?
    fun deleteAll()
}