package com.example.bookorder.order

import com.example.bookorder.core.AuditEntity
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(
    name = "order_event_tbl",
)
class OrderEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var orderId: Long,

    @Column(nullable = false)
    var totalAmount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var orderStatus: OrderStatus,

    createdAt: LocalDateTime = LocalDateTime.now(),
    updatedAt: LocalDateTime = LocalDateTime.now(),
    deletedAt: LocalDateTime? = null
) : AuditEntity(
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt
);