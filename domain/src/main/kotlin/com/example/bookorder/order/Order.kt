package com.example.bookorder.order

import com.example.bookorder.core.entity.BaseEntity
import com.example.bookorder.core.entity.EntityId
import com.example.bookorder.core.entity.Audit
import java.math.BigDecimal

@JvmInline
value class OrderId(override val value: Long) : EntityId<Long> {
    companion object {
        fun of(id: Long?): OrderId = id?.let { OrderId(it) }
            ?: throw IllegalArgumentException("${OrderId::class.simpleName} cannot be null")
    }
}


data class Order(
    override val id: OrderId? = null,
    val idempotencyKey: String,
    val totalAmount: BigDecimal,
    var status: OrderStatus,
    val orderItems: List<OrderItem>
): Audit(), BaseEntity<OrderId> {

    fun completeOrder() {
        if (status == OrderStatus.PAID) {
            throw IllegalStateException("Order already paid")
        }
        status = OrderStatus.PAID
    }

    fun failOrder() {
        status = OrderStatus.FAILED
    }

    fun markUncertain() {
        status = OrderStatus.PAYMENT_UNCERTAIN
    }
}

enum class OrderStatus {
    PENDING, PAID, FAILED, PAYMENT_UNCERTAIN
}