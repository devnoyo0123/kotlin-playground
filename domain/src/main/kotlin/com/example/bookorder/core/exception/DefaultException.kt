package com.example.bookorder.core.exception

open class DefaultException: RuntimeException {
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}