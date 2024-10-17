package com.example.bookorder.read

import com.example.bookorder.order.OrderStatus
import java.util.*

data class FindOrderByIdResponse(
    val orderId: UUID,
    val status: OrderStatus,
    val message: String
) {

}