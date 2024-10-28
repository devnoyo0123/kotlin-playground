package com.example.bookorder.order

import org.springframework.data.jpa.repository.JpaRepository

interface OrderEventEntityRepository : JpaRepository<OrderEventEntity, Long> {
}