package com.example.bookorder.order

import org.springframework.data.jpa.repository.JpaRepository

interface OrderCreatedEntityRepository : JpaRepository<OrderCreatedEventEntity, Long> {
}