package com.example.bookorder.payment

import com.example.bookorder.core.exception.DefaultException

class PaymentRequestTimeoutException: DefaultException {

    companion object {
        const val MESSAGE = "결제 요청 대기시간이 초과되었습니다."
    }

    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}