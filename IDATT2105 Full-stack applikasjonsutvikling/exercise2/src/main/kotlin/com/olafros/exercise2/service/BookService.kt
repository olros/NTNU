package com.olafros.exercise2.service

import com.olafros.exercise2.controller.BookController
import com.olafros.exercise2.exception.NotFoundException
import com.olafros.exercise2.model.*
import com.olafros.exercise2.repository.BookRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BookService(
    val bookRepository: BookRepository,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(BookController::class.java)
    }

    fun getBookById(bookId: Long): Book {
        val book = bookRepository.findBookByIsbn(bookId)
        return if (book != null) {
            book
        } else {
            logger.warn("Could not find book by id: $bookId")
            throw NotFoundException("Could not find the book")
        }
    }

    fun getBooks(name: String?, author: String?): List<Book> {
        val books = when {
            (name != null && author != null) -> {
                logger.info("Search for book by name: $name and author: $author")
                bookRepository.findBooksByNameContainsAndAuthors_NameContains(
                    name,
                    author
                )
            }
            (name != null) -> {
                logger.info("Search for book by name: $name")
                bookRepository.findBooksByNameContains(name)
            }
            (author != null) -> {
                logger.info("Search for book by author: $author")
                bookRepository.findByAuthors_NameContains(author)
            }
            else -> bookRepository.findAll()
        }
        return books
    }

    fun createNewBook(newBook: CreateBookDto): Book {
        val book = Book(newBook.isbn, newBook.name, newBook.year, newBook.publisher)
        return bookRepository.save(book)
    }

    fun updateBookById(
        bookId: Long,
        updatedBook: UpdateBookDto
    ): Book {
        val book = bookRepository.findBookByIsbn(bookId)
        return if (book != null) {
            val newBook = book.copy(
                name = updatedBook.name ?: book.name,
                year = updatedBook.year ?: book.year,
                publisher = updatedBook.publisher ?: book.publisher,
            )
            bookRepository.save(newBook)
        } else {
            logger.warn("Could not find book by id: $bookId")
            throw NotFoundException("Could not find the book to update")
        }
    }

    fun deleteBookById(bookId: Long): SuccessResponse {
        val book = bookRepository.findBookByIsbn(bookId)
        return if (book != null) {
            bookRepository.delete(book)
            SuccessResponse("Book successfully deleted")
        } else {
            logger.warn("Could not find book by id: $bookId")
            throw NotFoundException("Could not find the book to delete")
        }
    }
}