package ntnu.idatt2105.core.config

import ntnu.idatt2105.user.model.Role
import ntnu.idatt2105.user.model.RoleType
import ntnu.idatt2105.user.model.User
import ntnu.idatt2105.user.repository.RoleRepository
import ntnu.idatt2105.user.repository.UserRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*

@Component
class SetupDataLoader(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val passwordEncoder: BCryptPasswordEncoder
) : ApplicationListener<ContextRefreshedEvent?> {
    private var alreadySetup = false

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (!alreadySetup) {

            val userRole = roleRepository.findByName(RoleType.USER)
                ?: roleRepository.save(Role(id = UUID.randomUUID(), name = RoleType.USER))

            val adminRole = roleRepository.findByName(RoleType.ADMIN)
                ?: roleRepository.save(Role(id = UUID.randomUUID(), name = RoleType.ADMIN))

            userRepository.findByEmail("admin@test.com") ?: userRepository.save(
                User(id = UUID.randomUUID(),
                    firstName = "Admin",
                    surname = "User",
                    email = "admin@test.com",
                    phoneNumber = "+4712345678",
                    password = passwordEncoder.encode("admin"),
                    expirationDate = LocalDate.EPOCH,
                    roles = mutableSetOf(userRole, adminRole)
                ))

            userRepository.findByEmail("user@test.com") ?: userRepository.save(
                User(id = UUID.randomUUID(),
                    firstName = "Test",
                    surname = "User",
                    email = "user@test.com",
                    phoneNumber = "+test",
                    password = passwordEncoder.encode("user"),
                    expirationDate = LocalDate.now().plusYears(1),
                    roles = mutableSetOf(userRole)
                ))
        }
        alreadySetup = true
    }
}
