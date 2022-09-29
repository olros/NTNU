package ntnu.idatt2105.user.dto

import ntnu.idatt2105.user.model.User
import java.util.*

data class UserListDto(
    val id: UUID? = null,
    val firstName: String = "",
    val surname: String = "",
    val phoneNumber: String = "",
    val email: String = ""
)
fun User.toUserListDto() = UserListDto(
        id = this.id,
        firstName = this.firstName,
        surname = this.surname,
        email = this.email,
        phoneNumber = this.phoneNumber

)
