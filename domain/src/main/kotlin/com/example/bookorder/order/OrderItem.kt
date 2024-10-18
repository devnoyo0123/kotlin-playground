package com.example.bookorder.order

import com.example.bookorder.book.Book
import com.example.bookorder.book.BookId
import com.example.bookorder.core.entity.BaseEntity
import com.example.bookorder.core.entity.EntityId
import com.example.bookorder.core.entity.Audit
import java.math.BigDecimal

@JvmInline
value class OrderItemId(override val value: Long): EntityId<Long> {
    companion object {
        fun of(id: Long?): OrderItemId = id?.let { OrderItemId(it) } ?: throw IllegalArgumentException("${OrderItem::class.simpleName} cannot be null")
    }
}

data class OrderItem(
    override val id: OrderItemId? = null,
    val bookId: BookId,
    val quantity: Int,
    val price: BigDecimal
): Audit(), BaseEntity<OrderItemId> {

}