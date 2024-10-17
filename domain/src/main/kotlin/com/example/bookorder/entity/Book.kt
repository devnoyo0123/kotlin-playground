package com.example.bookorder.entity

import com.example.bookorder.core.BaseEntity
import com.example.bookorder.core.EntityId
import com.example.bookorder.exception.InsufficientStockException
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
    var stock: Int
): Audit(), BaseEntity<BookId> {

    fun decreaseStock(quantity: Int) {
        if (stock < quantity) {
            throw InsufficientStockException("Not enough stock for book: $title")
        }
        stock -= quantity
    }

    fun increaseStock(quantity: Int) {
        stock += quantity
    }
}
