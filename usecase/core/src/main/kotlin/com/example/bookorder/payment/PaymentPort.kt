package com.example.bookorder.payment

import com.example.bookorder.order.OrderId

interface PaymentPort {
    fun save(payment: Payment): Payment
    fun save(paymentEvent: PaymentEvent): PaymentEvent
    fun findById(paymentId: PaymentId): Payment?
    fun findByOrderId(orderId: OrderId): Payment?
}