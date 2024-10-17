package com.example.bookorder.read

interface FindOrderByIdUseCase {
    fun execute(orderId: OrderId): OrderResult
}