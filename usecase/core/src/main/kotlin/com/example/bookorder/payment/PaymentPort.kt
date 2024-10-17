package com.example.bookorder.payment

import com.example.bookorder.entity.Payment

interface PaymentPort {
    fun pay(orderId: String, price: Int): Boolean
    fun save(payment: Payment): Payment
}