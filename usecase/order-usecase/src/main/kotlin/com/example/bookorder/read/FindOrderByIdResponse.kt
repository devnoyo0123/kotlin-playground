package com.example.bookorder.read

import com.example.bookorder.order.OrderItem
import com.example.bookorder.order.OrderStatus
import java.math.BigDecimal

data class FindOrderByIdResponse(
    val orderId: Long,
    val totalAmount: BigDecimal,
    val status: OrderStatus,
    val orderItems: List<OrderItem>,
    val message: String = ""
)