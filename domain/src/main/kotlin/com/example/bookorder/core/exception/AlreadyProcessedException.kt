package com.example.bookorder.core.exception

class AlreadyProcessedException : DefaultException {
    companion object {
        const val MESSAGE = "이미 처리된 작업입니다."

        private const val MESSAGE_FORMAT = "이미 처리된 작업입니다. %s ID: %s"

        fun forId(id: Any, entityName: String): NotFoundException {
            val message = String.format(MESSAGE_FORMAT, entityName, id)
            return NotFoundException(message)
        }
    }

    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}