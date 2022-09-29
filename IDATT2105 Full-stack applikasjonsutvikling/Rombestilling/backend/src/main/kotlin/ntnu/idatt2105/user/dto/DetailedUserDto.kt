package ntnu.idatt2105.user.dto

import java.time.LocalDate
import java.util.*

data class DetailedUserDto(
    val id: UUID = UUID.randomUUID(),
    val firstName: String = "",
    val surname: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val image: String = "",
    val expirationDate: LocalDate = LocalDate.EPOCH,
    val roles: Set<RoleDto> = mutableSetOf()
)
