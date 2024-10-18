package com.example.bookorder.create.exception

import com.example.bookorder.core.exception.DefaultException

class OrderCreationException: DefaultException {

    companion object {
        private const val MESSAGE_FORMAT = "주문을 생성 할 수 없습니다. 주문 key: %s"

        fun forIdempotentKey(key: String, cause: Throwable): OrderCreationException {
            return OrderCreationException(String.format(MESSAGE_FORMAT, key), cause)
        }
    }

    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}