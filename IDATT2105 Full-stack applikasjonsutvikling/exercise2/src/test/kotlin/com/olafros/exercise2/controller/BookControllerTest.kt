package com.olafros.exercise2.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.olafros.exercise2.model.Book
import com.olafros.exercise2.model.CreateBookDto
import com.olafros.exercise2.model.UpdateBookDto
import com.olafros.exercise2.service.BookService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.Year


@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest(
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val mockMvc: MockMvc,
    @Autowired val bookService: BookService
) {

    fun getTestBook(isbn: Long) = Book(isbn, "Book $isbn", Year.now(), "Publisher $isbn", mutableListOf())

    private lateinit var book1: Book
    private lateinit var book2: Book

    @BeforeEach
    fun setUp() {
        val createBook1 = CreateBookDto(123, "Book 1", Year.now(), "Publisher 1")
        val createBook2 = CreateBookDto(234, "Book 2", Year.now(), "Publisher 2")
        book1 = bookService.createNewBook(createBook1)
        book2 = bookService.createNewBook(createBook2)
    }

    @Test
    fun `Get book dto by id`() {
        mockMvc.perform(get("/api/books/${book1.isbn}/").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.isbn").value(book1.isbn))
            .andExpect(jsonPath("\$.name").value(book1.name))
            .andExpect(jsonPath("\$.publisher").value(book1.publisher))
    }

    @Test
    fun `Get all books`() {
        mockMvc.perform(get("/api/books/").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.[0].isbn").value(book1.isbn))
            .andExpect(jsonPath("\$.[0].name").value(book1.name))
            .andExpect(jsonPath("\$.[0].publisher").value(book1.publisher))
            .andExpect(jsonPath("\$.[1].isbn").value(book2.isbn))
            .andExpect(jsonPath("\$.[1].name").value(book2.name))
            .andExpect(jsonPath("\$.[1].publisher").value(book2.publisher))
    }

    @Test
    fun createNewBook() {
        val newBook = CreateBookDto(345, "New book", Year.now(), "Publisher 3")
        mockMvc.perform(
            post("/api/books/").content(objectMapper.writeValueAsString(newBook))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.isbn").value(newBook.isbn))
            .andExpect(jsonPath("\$.name").value(newBook.name))
            .andExpect(jsonPath("\$.publisher").value(newBook.publisher))
    }

    @Test
    fun updateBookById() {
        val updatedBook = UpdateBookDto("New booktitle", null, null)
        mockMvc.perform(
            put("/api/books/${book1.isbn}/").content(objectMapper.writeValueAsString(updatedBook))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.name").value(updatedBook.name))
            .andExpect(jsonPath("\$.publisher").value(book1.publisher))
    }

    @Test
    fun deleteBookById() {
        mockMvc.perform(delete("/api/books/${book1.isbn}/").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    }
}