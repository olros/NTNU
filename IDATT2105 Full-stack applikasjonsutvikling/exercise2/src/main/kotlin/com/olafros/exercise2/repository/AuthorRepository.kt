package com.olafros.exercise2.repository

import com.olafros.exercise2.model.Author
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository : JpaRepository<Author, Long> {
    fun findAuthorById(id: Long?): Author?
    fun findAuthorsByNameContains(name: String): List<Author>
}