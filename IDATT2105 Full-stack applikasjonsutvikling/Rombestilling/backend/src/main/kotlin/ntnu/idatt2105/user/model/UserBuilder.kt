package ntnu.idatt2105.user.model

import java.time.LocalDate
import java.util.UUID

data class UserBuilder(
    val id: UUID = UUID.randomUUID(),
    val firstName: String = "",
    val surname: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val expirationDate: LocalDate = LocalDate.now(),
    val password: String = "",
    val roles: MutableSet<Role> = mutableSetOf()
) {
    fun build(): User {
        return User(
            id = id,
            firstName = firstName,
            surname = surname,
            email = email,
            phoneNumber = phoneNumber,
            expirationDate = expirationDate,
            password = password,
            roles = roles
        )
    }
}
