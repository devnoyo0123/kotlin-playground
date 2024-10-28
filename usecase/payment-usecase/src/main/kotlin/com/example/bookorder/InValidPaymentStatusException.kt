package com.example.bookorder

import com.example.bookorder.core.exception.DefaultException

class InValidPaymentStatusException: DefaultException {

    companion object {
        const val MESSAGE = "유효하지 않은 결제 상태입니다."
    }

    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}