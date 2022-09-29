package com.olafros.exercise2.controller

import com.olafros.exercise2.model.AuthorDtoList
import com.olafros.exercise2.model.CreateBookAuthorDto
import com.olafros.exercise2.model.toAuthorDtoList
import com.olafros.exercise2.service.BookAuthorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/books/{bookId}/authors")
class BookAuthorController(
    val bookAuthorService: BookAuthorService,
) {
    @GetMapping
    fun getBookAuthors(@PathVariable bookId: Long): ResponseEntity<List<AuthorDtoList>> {
        return ResponseEntity.ok(bookAuthorService.getBookAuthors(bookId).map { author -> author.toAuthorDtoList() })
    }

    @PostMapping
    fun createNewBookAuthor(
        @PathVariable bookId: Long,
        @Valid @RequestBody newAuthor: CreateBookAuthorDto
    ): ResponseEntity<List<AuthorDtoList>> {
        return ResponseEntity.ok(bookAuthorService.createNewBookAuthor(bookId, newAuthor).map { author -> author.toAuthorDtoList() })
    }

    @DeleteMapping("/{authorId}")
    fun deleteBookAuthorById(@PathVariable bookId: Long, @PathVariable authorId: Long): ResponseEntity<List<AuthorDtoList>> {
        return ResponseEntity.ok(bookAuthorService.deleteBookAuthorById(bookId, authorId).map { author -> author.toAuthorDtoList() })
    }
}
