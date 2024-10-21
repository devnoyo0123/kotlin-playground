package com.example.bookorder.create.exception

import com.example.bookorder.core.exception.DefaultException

class MaximumRetryException: DefaultException {
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}