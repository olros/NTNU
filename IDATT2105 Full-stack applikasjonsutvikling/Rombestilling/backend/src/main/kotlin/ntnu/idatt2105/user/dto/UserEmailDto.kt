package ntnu.idatt2105.user.dto

import java.util.*

data class UserEmailDto(
    val email: String = ""
)

fun UserEmailDto.toUserListDto() = UserListDto(
        email = this.email
)
