package com.example.bookorder.book

interface BookPort {
    fun save(book: Book): Book
    fun findById(id: BookId): Book?
}