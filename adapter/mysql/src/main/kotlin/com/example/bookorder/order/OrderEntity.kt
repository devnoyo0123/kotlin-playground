package com.example.bookorder.order

import com.example.bookorder.core.AuditEntity
import jakarta.persistence.*
import java.math.BigDecimal

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
    var orderItems: MutableList<OrderItemEntity> = mutableListOf()
) : AuditEntity() {

    fun addOrderItem(orderItem: OrderItemEntity) {
        orderItems.add(orderItem)
        orderItem.mapOrderEntity(this)
    }

}