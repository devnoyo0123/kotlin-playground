package com.example.bookorder.update

import com.example.bookorder.book.BookPort
import com.example.bookorder.order.Order
import com.example.bookorder.order.OrderPort
import com.example.bookorder.payment.PaymentStatus
import org.springframework.stereotype.Component


@Component
class OrderFailureProcessor(
    private val orderPort: OrderPort,
    private val bookPort: BookPort
) : OrderProcessor {
    override fun process(order: Order, paymentStatus: PaymentStatus): Order {
        order.orderItems.forEach { item ->
            val book = bookPort.findById(item.bookId) ?: throw IllegalStateException("Book not found")
            book.increaseStock(item.quantity)
            bookPort.save(book)
        }
        order.fail()
        return orderPort.save(order)
    }

    override fun support(paymentStatus: PaymentStatus): Boolean {
        return paymentStatus == PaymentStatus.FAILED
    }
}