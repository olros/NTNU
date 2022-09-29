package com.olafros.exercise2.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Year
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "books")
data class Book(
    @Id
    var isbn: @NotNull Long,
    var name: @NotNull @Size(max = 128) String,
    var year: @NotNull Year,
    var publisher: @NotNull @Size(max = 128) String,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "author_books",
        joinColumns = [JoinColumn(name = "book_id", referencedColumnName = "isbn")],
        inverseJoinColumns = [JoinColumn(name = "author_id", referencedColumnName = "id")],
        uniqueConstraints = [UniqueConstraint(columnNames = ["book_id", "author_id"])]
    )
    @JsonBackReference
    @JsonIgnore
    var authors: MutableList<Author> = mutableListOf(),
)

data class BookDto(
    val isbn: Long,
    val name: String,
    val year: Year,
    val publisher: String,
    val authors: List<AuthorDtoList>,
)

data class BookDtoList(
    val isbn: Long,
    val name: String,
    val year: Year,
    val publisher: String,
)

data class CreateBookDto(val isbn: Long, val name: String, val year: Year, val publisher: String)
data class UpdateBookDto(val name: String?, val year: Year?, val publisher: String?)
data class CreateBookAuthorDto(val authorId: Long)

fun Book.toBookDto(): BookDto {
    return BookDto(
        this.isbn,
        this.name,
        this.year,
        this.publisher,
        this.authors.map { author -> author.toAuthorDtoList() },
    )
}

fun Book.toBookDtoList(): BookDtoList {
    return BookDtoList(
        this.isbn,
        this.name,
        this.year,
        this.publisher,
    )
}
