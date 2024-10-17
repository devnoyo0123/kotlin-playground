package com.example.bookorder.payment

interface PaymentPort {
    fun pay(orderId: String, price: Int): Boolean
    fun save(payment: Payment): Payment
}