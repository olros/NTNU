package com.olafros.exercise2.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "authors")
data class Author(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: @NotNull @Size(max = 128) String,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    var address: Address? = null,

    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
    var books: MutableList<Book> = mutableListOf(),
)

data class AuthorDto(
    val id: Long,
    val name: String,
    val address: AddressDto?,
    val books: List<BookDtoList>,
)

data class AuthorDtoList(val id: Long, val name: String)
data class CreateAuthorDto(val name: String, val address: CreateAddressDto)
data class UpdateAuthorDto(val name: String?, val address: CreateAddressDto?)

fun Author.toAuthorDto(): AuthorDto {
    return AuthorDto(
        this.id,
        this.name,
        this.address?.toAddressDto(),
        this.books.map { book -> book.toBookDtoList() },
    )
}

fun Author.toAuthorDtoList(): AuthorDtoList {
    return AuthorDtoList(this.id, this.name)
}