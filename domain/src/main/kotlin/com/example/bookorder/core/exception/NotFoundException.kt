package com.example.bookorder.core.exception

class NotFoundException: DefaultException {

    companion object {
        const val MESSAGE = "존재하지 않는 데이터입니다."
        private const val MESSAGE_FORMAT = "존재하지 않는 데이터입니다. %s ID: %s"

        fun forId(id: Any, entityName: String): NotFoundException {
            val message = String.format(MESSAGE_FORMAT, entityName, id)
            return NotFoundException(message)
        }
    }


    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}