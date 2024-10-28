package com.example.bookorder.payment

import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository: JpaRepository<PaymentEntity, Long> {
    fun findByOrderId(orderId: Long): PaymentEntity?
}