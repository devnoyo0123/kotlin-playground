package com.example.bookorder.book.exception

import com.example.bookorder.core.exception.DefaultException


class InsufficientStockException: DefaultException {
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}
