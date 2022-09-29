package ntnu.idatt2105.user.service

import ntnu.idatt2105.core.exception.ApplicationException
import ntnu.idatt2105.core.exception.EntityType
import ntnu.idatt2105.core.exception.ExceptionType
import ntnu.idatt2105.user.model.User
import ntnu.idatt2105.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class UserDetailsServiceImpl : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user: User = userRepository.findByEmail(email) ?: throw ApplicationException.throwException(
            EntityType.USER, ExceptionType.ENTITY_NOT_FOUND, "2", email)
        return UserDetailsImpl(user.id, user.email, user.password, getAuthorities(user))
    }

    private fun getAuthorities(user: User) =
        user.roles.map { role -> SimpleGrantedAuthority("ROLE_".plus(role.name)) }
            .toCollection(mutableSetOf())
}
