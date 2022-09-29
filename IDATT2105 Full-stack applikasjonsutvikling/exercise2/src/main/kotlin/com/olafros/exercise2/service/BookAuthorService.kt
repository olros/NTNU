package com.olafros.exercise2.service

import com.olafros.exercise2.exception.NotFoundException
import com.olafros.exercise2.model.Author
import com.olafros.exercise2.model.AuthorDtoList
import com.olafros.exercise2.model.CreateBookAuthorDto
import com.olafros.exercise2.model.toAuthorDtoList
import com.olafros.exercise2.repository.AuthorRepository
import com.olafros.exercise2.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookAuthorService(
    val authorRepository: AuthorRepository,
    val bookRepository: BookRepository,
) {
    fun getBookAuthors(bookId: Long): List<Author> {
        val book = bookRepository.findBookByIsbn(bookId)
        return if (book != null) {
            book.authors
        } else {
            throw NotFoundException("Could not find the book")
        }
    }

    fun createNewBookAuthor(bookId: Long, newAuthor: CreateBookAuthorDto): List<Author> {
        val book = bookRepository.findBookByIsbn(bookId)
        val author = authorRepository.findAuthorById(newAuthor.authorId)
        return when {
            (book == null) ->
                throw NotFoundException("Could not find the book")
            (author == null) ->
                throw NotFoundException("Could not find the author")
            (book.authors.any { bookAuthor -> bookAuthor.id == author.id }) ->
                throw NotFoundException("The auther is already auther")
            else -> {
                book.authors.add(author)
                bookRepository.save(book)
                book.authors
            }
        }
    }

    fun deleteBookAuthorById(bookId: Long, authorId: Long): List<Author> {
        val book = bookRepository.findBookByIsbn(bookId)
        return if (book != null) {
            val author = book.authors.find { bookAuthor -> bookAuthor.id == authorId }
            if (author != null) {
                book.authors.remove(author)
                bookRepository.save(book)
                book.authors
            } else {
                throw NotFoundException("Could not find the author to remove")
            }
        } else {
            throw NotFoundException("Could not find the book")
        }
    }
}
