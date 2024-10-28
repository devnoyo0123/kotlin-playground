package com.example.bookorder.order

import com.example.bookorder.core.entity.Audit
import com.example.bookorder.core.entity.BaseEntity
import com.example.bookorder.core.entity.EntityId
import java.math.BigDecimal

@JvmInline
value class OrderEventId(override val value: Long) : EntityId<Long> {
    companion object {
        fun of(id: Long?): OrderEventId = id?.let { OrderEventId(it) }
            ?: throw IllegalArgumentException("${OrderEventId::class.simpleName} cannot be null")
    }
}


data class OrderEvent(
    override val id: OrderEventId? = null,
    val orderId: OrderId,
    val orderStatus: OrderStatus,
    val totalAmount: BigDecimal,
) : Audit(), BaseEntity<OrderEventId>