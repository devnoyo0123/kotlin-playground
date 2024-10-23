package com.example.bookorder.order

interface OrderPort {
    fun findById(id: OrderId): Order?
    fun save(order: Order): Order
    fun save(orderCreatedEvent: OrderCreatedEvent): OrderCreatedEvent
    fun findByIdempotencyKey(idempotencyKey: String): Order?
    fun deleteAll()
}