package com.olafros.exercise2.repository

import com.olafros.exercise2.model.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : JpaRepository<Book, Long> {
    fun findBookByIsbn(isbn: Long?): Book?
    fun findBooksByNameContains(name: String): List<Book>
    fun findByAuthors_NameContains(name: String): List<Book>
    fun findBooksByNameContainsAndAuthors_NameContains(name: String, authorName: String): List<Book>
}