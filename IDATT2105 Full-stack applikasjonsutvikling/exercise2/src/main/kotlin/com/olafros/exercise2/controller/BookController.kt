package com.olafros.exercise2.controller

import com.olafros.exercise2.model.*
import com.olafros.exercise2.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/books")
class BookController(
    val bookService: BookService,
) {


    @GetMapping("/{bookId}")
    fun getBookById(@PathVariable bookId: Long): ResponseEntity<BookDto> {
        return ResponseEntity.ok(bookService.getBookById(bookId).toBookDto())
    }

    @GetMapping
    fun getBooks(@RequestParam name: String?, @RequestParam author: String?): ResponseEntity<List<BookDtoList>> {
        return ResponseEntity.ok(bookService.getBooks(name, author).map { book -> book.toBookDtoList() })
    }

    @PostMapping
    fun createNewBook(@Valid @RequestBody newBook: CreateBookDto): ResponseEntity<BookDto> {
        return ResponseEntity.ok(bookService.createNewBook(newBook).toBookDto())
    }

    @PutMapping("/{bookId}")
    fun updateBookById(
        @PathVariable bookId: Long,
        @Valid @RequestBody updatedBook: UpdateBookDto
    ): ResponseEntity<BookDto> {
        return ResponseEntity.ok(bookService.updateBookById(bookId, updatedBook).toBookDto())
    }

    @DeleteMapping("/{bookId}")
    fun deleteBookById(@PathVariable bookId: Long): ResponseEntity<*> {
        return ResponseEntity.ok(bookService.deleteBookById(bookId))
    }
}
