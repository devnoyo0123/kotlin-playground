package com.example.bookorder.order

import com.example.bookorder.entity.Order
import com.example.bookorder.entity.OrderId

interface OrderPort {
    fun findById(id: OrderId): Order?
    fun save(order: Order): Order
}