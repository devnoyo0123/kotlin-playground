package com.example.bookorder.payment

interface PaymentRequestPort {
    fun requestPayment(payment: Payment): PaymentStatus
    fun getPaymentStatus(paymentId: PaymentId): PaymentStatus?
    fun requestCancel(payment: Payment): PaymentStatus
}