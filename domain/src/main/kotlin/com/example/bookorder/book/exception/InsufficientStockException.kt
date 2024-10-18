package com.example.bookorder.book.exception

import com.example.bookorder.book.BookId
import com.example.bookorder.core.exception.DefaultException


class InsufficientStockException: DefaultException {

    companion object {
        private const val MESSAGE_FORMAT = "주문을 찾을 수 없습니다. 주문 ID: %s"

        fun forBookId(bookId: BookId): InsufficientStockException {
            return InsufficientStockException(String.format(MESSAGE_FORMAT, bookId.value))
        }
    }

    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}
