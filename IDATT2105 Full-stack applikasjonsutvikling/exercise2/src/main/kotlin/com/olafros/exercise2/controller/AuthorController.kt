package com.olafros.exercise2.controller

import com.olafros.exercise2.model.*
import com.olafros.exercise2.service.AuthorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/authors")
class AuthorController(
    val authorService: AuthorService,
) {

    @GetMapping("/{authorId}")
    fun getAuthorById(@PathVariable authorId: Long): ResponseEntity<AuthorDto> {
        return ResponseEntity.ok(authorService.getAuthorById(authorId).toAuthorDto())
    }

    @GetMapping
    fun getAuthors(@RequestParam name: String?): ResponseEntity<List<AuthorDtoList>> {
        return ResponseEntity.ok(authorService.getAuthors(name).map { author -> author.toAuthorDtoList() })
    }

    @PostMapping
    fun createNewAuthor(@Valid @RequestBody newAuthor: CreateAuthorDto): ResponseEntity<AuthorDto> {
        return ResponseEntity.ok(authorService.createNewAuthor(newAuthor).toAuthorDto())
    }

    @PutMapping("/{authorId}")
    fun updateAuthorById(
        @PathVariable authorId: Long,
        @Valid @RequestBody updatedAuthor: UpdateAuthorDto
    ): ResponseEntity<AuthorDto> {
        return ResponseEntity.ok(authorService.updateAuthorById(authorId, updatedAuthor).toAuthorDto())
    }

    @DeleteMapping("/{authorId}")
    fun deleteAuthorById(@PathVariable authorId: Long): ResponseEntity<SuccessResponse> {
        return ResponseEntity.ok(authorService.deleteAuthorById(authorId))
    }
}
