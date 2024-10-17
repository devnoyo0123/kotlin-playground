package com.example.bookorder.read

import com.example.bookorder.entity.OrderStatus
import java.util.*

data class FindOrderByIdResponse(
    val orderId: UUID,
    val status: OrderStatus,
    val message: String
) {

}