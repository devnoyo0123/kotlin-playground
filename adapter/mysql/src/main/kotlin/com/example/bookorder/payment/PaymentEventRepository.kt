package com.example.bookorder.payment

import org.springframework.data.jpa.repository.JpaRepository

interface PaymentEventRepository: JpaRepository<PaymentEventEntity, Long>