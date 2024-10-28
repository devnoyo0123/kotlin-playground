package com.example.bookorder

import com.example.bookorder.payment.Payment

/**
 * 결제 실패 시 처리하는 유스케이스입니다.
 * 주문 취소 이벤트 생성합니다.
 */
interface PaymentFailureUseCase {
    fun execute(payment: Payment): Payment
}
