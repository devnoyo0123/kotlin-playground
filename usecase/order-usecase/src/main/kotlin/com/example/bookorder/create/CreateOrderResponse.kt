package com.example.bookorder.create

import com.example.bookorder.entity.OrderStatus
import java.util.*

data class CreateOrderResponse(
    val orderId: UUID,
    val status: OrderStatus
)
