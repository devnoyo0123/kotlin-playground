package com.example.bookorder.read

import com.example.bookorder.entity.OrderId

interface FindOrderByIdUseCase {
    fun execute(orderId: OrderId): FindOrderByIdResponse
}