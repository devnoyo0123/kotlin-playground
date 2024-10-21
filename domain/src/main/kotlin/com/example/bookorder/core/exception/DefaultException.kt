package com.example.bookorder.core.exception

open class DefaultException: RuntimeException {

    companion object {
        private const val MESSAGE_FORMAT = "주문을 찾을 수 없습니다. 주문 ID: %s"
    }

    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}