package com.example.bookorder.create

import com.example.bookorder.order.OrderId
import com.example.bookorder.order.OrderStatus

interface CreateOrderUseCase {
    /**
     * @description
     * 주문 생성하여 생성된 주문을 반환합니다.
     */
    fun execute(request: CreateOrderRequest): CreateOrderResponse
}



data class CreateOrderRequest(
    val idempotencyKey: String,
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val bookId: Long,
    val quantity: Int
)

data class CreateOrderResponse(
    val orderId: OrderId,
    val status: OrderStatus,
    val message: String = ""
)
