package com.example.bookorder

import com.example.bookorder.payment.PaymentEvent

interface HandlePaymentProcessingDLQUseCase {
    fun cancelPayment(paymentEvent: PaymentEvent)
}