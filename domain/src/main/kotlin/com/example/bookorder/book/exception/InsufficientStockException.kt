package com.example.bookorder.book.exception

import com.example.bookorder.core.exception.DefaultException


class InsufficientStockException(message: String) : DefaultException(message)
