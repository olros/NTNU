package ntnu.idatt2105.user.service

import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.UUID

data class UserDetailsImplBuilder(
    val id: UUID = UUID.randomUUID(),
    val email: String = "",
    val password: String = "",
    val roles: Set<SimpleGrantedAuthority> = setOf()
) {
    fun build(): UserDetailsImpl {
        return UserDetailsImpl(
            id = id,
            email = email,
            password = password,
            roles = roles
        )
    }
}
