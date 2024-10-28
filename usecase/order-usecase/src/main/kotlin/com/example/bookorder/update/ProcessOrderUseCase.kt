package com.example.bookorder.update

import com.example.bookorder.order.Order
import com.example.bookorder.payment.PaymentEvent

/**
 * 결제 이벤트 컨슈밍하여
 * 주문 상태를 처리하는 유스케이스입니다.
 */
interface ProcessOrderUseCase {
    fun processOrder(paymentEvent: PaymentEvent): Order
}
