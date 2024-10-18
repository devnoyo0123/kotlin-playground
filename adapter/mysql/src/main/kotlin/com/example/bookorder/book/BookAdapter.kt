package com.example.bookorder.book

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class BookAdapter(private val bookRepository: BookRepository) : BookPort {
    override fun save(book: Book): Book {
        val bookEntity = BookEntityConverter.toBookEntity(book)
        val savedEntity = bookRepository.save(bookEntity)
        return BookEntityConverter.toBookModel(savedEntity)
    }

    override fun findById(id: BookId): Book? {
        return bookRepository.findByIdOrNull(id.value)?.let {
            BookEntityConverter.toBookModel(it)
        }
    }

    override fun findByIds(bookIds: List<BookId>): List<Book> {
        return bookRepository.findAllById(bookIds.map { it -> it.value }).let { entities ->
            entities.map { BookEntityConverter.toBookModel(it) }
        }
    }
}

object BookEntityConverter {
    fun toBookEntity(book: Book): BookEntity {
        return BookEntity(
            id = book.id?.value,
            title = book.title,
            author = book.author,
            price = book.price,
            stock = book.stock
        ).apply {
            createdAt = book.createdAt
            updatedAt = book.updatedAt
            deletedAt = book.deletedAt
        }
    }

    fun toBookModel(entity: BookEntity): Book {
        return Book(
            id = BookId.of(entity.id),
            title = entity.title,
            author = entity.author,
            price = entity.price,
            stock = entity.stock
        ).apply {
            createdAt = entity.createdAt
            updatedAt = entity.updatedAt
            deletedAt = entity.deletedAt
        }
    }
}