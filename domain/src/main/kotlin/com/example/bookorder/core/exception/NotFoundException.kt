package com.example.bookorder.core.exception

class NotFoundException: DefaultException {

    companion object {
        const val MESSAGE = "존재 하지 않는 데이터 입니다."
    }

    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}