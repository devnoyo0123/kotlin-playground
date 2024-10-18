package com.example.bookorder.book

import com.example.bookorder.core.entity.BaseEntity
import com.example.bookorder.core.entity.EntityId
import com.example.bookorder.book.exception.InsufficientStockException
import com.example.bookorder.core.entity.Audit
import java.math.BigDecimal

@JvmInline
value class BookId(override val value: Long): EntityId<Long> {
    companion object {
        fun of(id: Long?): BookId = id?.let { BookId(it) } ?: throw IllegalArgumentException("${BookId::class.simpleName} cannot be null")
    }
}

data class Book(
    override val id: BookId? = null,
    val title: String,
    val author: String,
    val price: BigDecimal,
    var stock: Int,
    val version: Long = 0  // 버전 필드 추가
): Audit(), BaseEntity<BookId> {

    fun decreaseStock(quantity: Int) {
        if (stock < quantity) {
            throw InsufficientStockException.forBookId(getEntityId())
        }
        stock -= quantity
    }

    fun canFulfillOrder(quantity: Int): Boolean {
        return stock >= quantity
    }

}
