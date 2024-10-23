package com.example.bookorder.order

import com.example.bookorder.core.entity.Audit
import com.example.bookorder.core.entity.BaseEntity
import com.example.bookorder.core.entity.EntityId
import java.math.BigDecimal

@JvmInline
value class OrderCreatedEventId(override val value: Long) : EntityId<Long> {
    companion object {
        fun of(id: Long?): OrderCreatedEventId = id?.let { OrderCreatedEventId(it) }
            ?: throw IllegalArgumentException("${OrderCreatedEventId::class.simpleName} cannot be null")
    }
}


data class OrderCreatedEvent(
    override val id: OrderCreatedEventId? = null,
    val orderId: OrderId,
    val orderStatus: OrderStatus,
    val totalAmount: BigDecimal,
) : Audit(), BaseEntity<OrderCreatedEventId>