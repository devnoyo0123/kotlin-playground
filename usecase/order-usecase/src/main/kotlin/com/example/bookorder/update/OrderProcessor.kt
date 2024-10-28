package com.example.bookorder.update

import com.example.bookorder.order.Order
import com.example.bookorder.payment.PaymentStatus

interface OrderProcessor {
    fun process(order: Order, paymentStatus: PaymentStatus): Order
    fun support(paymentStatus: PaymentStatus): Boolean
}