package com.example.bookorder

open class DefaultException(
    override val message: String,
    val errorCode: String? = null
) : RuntimeException(message) {
    constructor(message: String) : this(message, null)
}
