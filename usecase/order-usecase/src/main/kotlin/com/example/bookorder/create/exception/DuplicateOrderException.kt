package com.example.bookorder.create.exception

import com.example.bookorder.core.exception.DefaultException

class DuplicateOrderException: DefaultException {

    companion object {

        const val MESSAGE = "이미 처리중인 요청입니다."

        private const val MESSAGE_FORMAT = "해당 멱등성 키로 요청이 이미 진행 중입니다. idempotentKey: %s"

        fun forIdempotentKey(idempotentKey: String): DuplicateOrderException {
            return DuplicateOrderException(String.format(MESSAGE_FORMAT, idempotentKey))
        }
    }

    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}