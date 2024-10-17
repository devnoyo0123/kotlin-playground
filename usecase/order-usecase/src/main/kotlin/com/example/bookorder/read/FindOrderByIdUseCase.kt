package com.example.bookorder.read

import com.example.bookorder.order.OrderId

interface FindOrderByIdUseCase {
    /**
     * @description
     * 주문 ID로 주문을 조회합니다.
     */
    fun execute(orderId: OrderId): FindOrderByIdResponse
}