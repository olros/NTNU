package com.olafros.exercise2.service

import com.ninjasquad.springmockk.MockkBean
import com.olafros.exercise2.model.Book
import com.olafros.exercise2.model.CreateBookDto
import com.olafros.exercise2.model.SuccessResponse
import com.olafros.exercise2.model.UpdateBookDto
import com.olafros.exercise2.repository.BookRepository
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import java.time.Year

@SpringBootTest
@AutoConfigureMockMvc
internal class BookServiceTest {
    @Autowired
    private lateinit var bookService: BookService

    @MockkBean
    private lateinit var repo: BookRepository

    fun getTestBook(isbn: Long) = Book(isbn, "Book $isbn", Year.now(), "Publisher $isbn", mutableListOf())
    val book1 = getTestBook(123)
    val book2 = getTestBook(234)

    @BeforeEach
    fun setUp() {
        every { repo.findAll() }.returns(arrayListOf(book1, book2))
        every { repo.findBookByIsbn(book1.isbn) }.returns(book1)
    }

    @Test
    fun `Get a book by isbn`() {
        assert(bookService.getBookById(book1.isbn).isbn == book1.isbn)
    }

    @Test
    fun `Get all books`() {
        val books = bookService.getBooks(null, null)
        assert(books[0].isbn == book1.isbn)
        assert(books[1].isbn == book2.isbn)
    }

    @Test
    fun `Get books by name`() {
        val books = bookService.getBooks(book2.name, null)
        assert(books[0].name == book2.name)
    }

    @Test
    fun createNewBook() {
        val newBook = CreateBookDto(1, "New book", Year.now(), "New publisher")
        every { repo.save(any()) }.returns(
            Book(
                newBook.isbn,
                newBook.name,
                newBook.year,
                newBook.publisher,
                arrayListOf()
            )
        )
        assert(bookService.createNewBook(newBook).isbn == newBook.isbn)
    }

    @Test
    fun updateBookById() {
        val newName = "New book"
        val newBook = UpdateBookDto(newName, null, null)
        every { repo.save(any()) }.returns(Book(book1.isbn, newName, book1.year, book1.publisher, arrayListOf()))
        assert(bookService.updateBookById(book1.isbn, newBook).name == newBook.name)
    }

    @Test
    fun deleteBookById() {
        every { repo.delete(any()) }.returns(book1)
        assert(bookService.deleteBookById(book1.isbn) == SuccessResponse("Book successfully deleted"))
    }
}