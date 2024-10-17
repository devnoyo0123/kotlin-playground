package com.example.bookorder.read.exception

import com.example.bookorder.core.exception.DefaultException
import com.example.bookorder.order.OrderId

class OrderNotFoundException: DefaultException {
    companion object {
        private const val MESSAGE_FORMAT = "주문을 찾을 수 없습니다. 주문 ID: %s"

        fun forOrderId(orderId: OrderId): OrderNotFoundException {
            return OrderNotFoundException(String.format(MESSAGE_FORMAT, orderId.value))
        }
    }

    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}
