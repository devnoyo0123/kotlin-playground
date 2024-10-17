package com.example.bookorder.book

import com.example.bookorder.entity.Book
import com.example.bookorder.entity.BookId

interface BookPort {
    fun save(book: Book): Book
    fun findById(id: BookId): Book?
}