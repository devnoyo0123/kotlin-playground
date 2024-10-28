package com.example.bookorder.order

import com.example.bookorder.core.AuditEntity
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(
    name = "order_tbl",
    indexes = [
        Index(name = "idx_order_idempotency_key", columnList = "idempotency_key")
    ])
class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var idempotencyKey: String,

    @Column(nullable = false)
    var totalAmount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus,

    @OneToMany(mappedBy = "orderEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var orderItems: MutableList<OrderItemEntity> = mutableListOf(),
    createdAt: LocalDateTime = LocalDateTime.now(),
    updatedAt: LocalDateTime = LocalDateTime.now(),
    deletedAt: LocalDateTime? = null
) : AuditEntity(createdAt, updatedAt, deletedAt) {

    fun addOrderItem(orderItem: OrderItemEntity) {
        orderItems.add(orderItem)
        orderItem.mapOrderEntity(this)
    }

}