package com.olafros.exercise2.model

import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "addresses")
data class Address(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var street: @NotNull @Size(max = 128) String,
    var post_nr: @NotNull @Size(max = 4) String,
    var city: @NotNull @Size(max = 128) String,
    var country: @NotNull @Size(max = 128) String,

    @OneToOne(mappedBy = "address")
    var author: Author?,
)

data class AddressDto(
    val street: String,
    val post_nr: String,
    val city: String,
    val country: String,
)

data class CreateAddressDto(
    val street: String,
    val post_nr: String,
    val city: String,
    val country: String,
)

fun Address.toAddressDto(): AddressDto {
    return AddressDto(
        this.street,
        this.post_nr,
        this.city,
        this.country,
    )
}
