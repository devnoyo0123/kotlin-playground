package com.example.bookorder.create.exception

import com.example.bookorder.core.exception.DefaultException

class OrderCreationException: DefaultException {
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}