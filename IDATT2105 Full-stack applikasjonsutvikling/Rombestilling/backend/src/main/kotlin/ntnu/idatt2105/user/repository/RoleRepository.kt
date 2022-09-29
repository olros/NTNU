package ntnu.idatt2105.user.repository

import ntnu.idatt2105.user.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RoleRepository : JpaRepository<Role, UUID> {
    fun findByName(name: String): Role?
}
