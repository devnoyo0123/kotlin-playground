package com.example.bookorder.order

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderEntityRepository : JpaRepository<OrderEntity, Long> {
    fun findByIdempotencyKey(idempotencyKey: String): OrderEntity?
}