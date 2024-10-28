package com.example.bookorder

import com.example.bookorder.payment.Payment


/**
 * 결제 성공 시 처리하는 유스케이스입니다.
 * 주문 완료 이벤트 생성합니다.
 */
interface PaymentSuccessUseCase {
    fun execute(payment: Payment): Payment
}
