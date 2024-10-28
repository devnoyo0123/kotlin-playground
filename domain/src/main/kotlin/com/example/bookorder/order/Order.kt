package com.example.bookorder.order

import com.example.bookorder.core.entity.BaseEntity
import com.example.bookorder.core.entity.EntityId
import com.example.bookorder.core.entity.Audit
import com.example.bookorder.core.exception.AlreadyProcessedException
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

    fun complete() {
        if (status == OrderStatus.PAID) {
            throw AlreadyProcessedException.forId(getEntityIdOrThrow(), Order::class.simpleName.toString())
        }
        status = OrderStatus.PAID
    }

    fun fail() {
        if (status == OrderStatus.FAILED) {
            throw AlreadyProcessedException.forId(getEntityIdOrThrow(), Order::class.simpleName.toString())
        }
        status = OrderStatus.FAILED
    }

    fun isCompleted(): Boolean {
        return status == OrderStatus.PAID
    }

    fun isFailed(): Boolean {
        return status == OrderStatus.FAILED
    }

}

enum class OrderStatus {
    PENDING, PAID, FAILED
}