package com.example.bookorder.payment

import com.example.bookorder.core.AuditEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "payment_event_tbl",
)
class PaymentEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var paymentId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PaymentStatus,

    createdAt: LocalDateTime = LocalDateTime.now(),
    updatedAt: LocalDateTime = LocalDateTime.now(),
    deletedAt: LocalDateTime? = null
) : AuditEntity(
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt
);