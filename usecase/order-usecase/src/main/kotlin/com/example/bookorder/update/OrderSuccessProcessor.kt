package com.example.bookorder.update

import com.example.bookorder.order.Order
import com.example.bookorder.order.OrderPort
import com.example.bookorder.payment.PaymentStatus
import org.springframework.stereotype.Component

@Component
class OrderSuccessProcessor(private val orderPort: OrderPort) : OrderProcessor {
    override fun process(order: Order, paymentStatus: PaymentStatus): Order {
        order.complete()
        return orderPort.save(order)
    }

    override fun support(paymentStatus: PaymentStatus): Boolean {
        return paymentStatus == PaymentStatus.COMPLETED
    }
}