package com.example.bookorder.read

import com.example.bookorder.order.OrderStatus

data class FindOrderByIdResponse(
    val orderId: Long,
    val status: OrderStatus,
    val message: String? = null
) {

}