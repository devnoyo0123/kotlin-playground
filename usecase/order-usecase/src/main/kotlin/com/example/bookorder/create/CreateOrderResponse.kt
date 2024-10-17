package com.example.bookorder.create

import com.example.bookorder.order.OrderId
import com.example.bookorder.order.OrderStatus

data class CreateOrderResponse(
    val orderId: OrderId,
    val status: OrderStatus
)
