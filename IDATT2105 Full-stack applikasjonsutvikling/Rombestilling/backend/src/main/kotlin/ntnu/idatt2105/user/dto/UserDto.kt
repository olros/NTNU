package ntnu.idatt2105.user.dto

import ntnu.idatt2105.user.model.User
import java.time.LocalDate
import java.util.*

data class UserDto(
    val id: UUID = UUID.randomUUID(),
    val firstName: String = "",
    val surname: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val image: String = "",
    val expirationDate: LocalDate = LocalDate.EPOCH
)

fun User.toUserDto() = UserDto(id = this.id, firstName = this.firstName, surname = this.surname, email = this.email,
        phoneNumber = this.phoneNumber, image = this.image, expirationDate = this.expirationDate)
