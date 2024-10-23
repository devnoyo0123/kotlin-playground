package com.example.bookorder.order

import com.example.bookorder.core.AuditEntity
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(
    name = "orderitem_tbl", indexes = [
        Index(name = "idx_orderitem_book_id", columnList = "book_id"),
        Index(name = "idx_orderitem_order_id", columnList = "order_id")
    ]
)
class OrderItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var orderEntity: OrderEntity? = null,

    @Column(name = "book_id", nullable = false)
    var bookId: Long,

    @Column(nullable = false)
    var quantity: Int,

    @Column(nullable = false)
    var price: BigDecimal,
    createdAt: LocalDateTime = LocalDateTime.now(),
    updatedAt: LocalDateTime = LocalDateTime.now(),
    deletedAt: LocalDateTime? = null
) : AuditEntity() {
    fun mapOrderEntity(orderEntity: OrderEntity) {
        this.orderEntity = orderEntity
    }
}