package com.example.bookorder

import com.example.bookorder.order.OrderEvent
import com.example.bookorder.payment.Payment

/**
 * 결제를 처리하는 유스케이스입니다.
 * 주문 생성시 발행된 이벤트를 컨슈밍하여 결제 요청합니다.
 */
interface ProcessPaymentUseCase {
    fun processPayment(orderEvent: OrderEvent): Payment
}