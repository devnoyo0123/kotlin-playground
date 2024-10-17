package com.example.bookorder.create

interface CreateOrderUseCase {
    fun execute(request: CreateOrderRequest): CreateOrderResponse
}