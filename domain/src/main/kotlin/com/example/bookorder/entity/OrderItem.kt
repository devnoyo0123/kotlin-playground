package com.example.bookorder.entity

import com.example.bookorder.core.BaseEntity
import com.example.bookorder.core.EntityId
import java.math.BigDecimal

@JvmInline
value class OrderItemId(override val value: Long): EntityId<Long> {
    companion object {
        fun of(id: Long?): OrderItemId = id?.let { OrderItemId(it) } ?: throw IllegalArgumentException("${OrderItem::class.simpleName} cannot be null")
    }
}

data class OrderItem(
    override val id: OrderItemId? = null,
    val book: Book,
    val quantity: Int,
    val price: BigDecimal
): Audit(), BaseEntity<OrderItemId> {

    fun decreaseStock() {
        book.decreaseStock(quantity)
    }
}