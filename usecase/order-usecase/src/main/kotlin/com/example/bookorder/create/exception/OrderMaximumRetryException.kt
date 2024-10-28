package com.example.bookorder.create.exception

import com.example.bookorder.core.exception.DefaultException

class OrderMaximumRetryException: DefaultException {

    companion object {
        const val MESSAGE = "주문 재시도 제한을 초과했습니다."
    }

    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}