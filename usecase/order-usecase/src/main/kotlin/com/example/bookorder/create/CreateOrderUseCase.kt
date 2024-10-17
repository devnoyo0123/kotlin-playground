package com.example.bookorder.create

interface CreateOrderUseCase {
    /**
     * @description
     * 주문 생성하여 생성된 주문을 반환합니다.
     */
    fun execute(request: CreateOrderRequest): CreateOrderResponse
}