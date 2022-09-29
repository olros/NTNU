package ntnu.idatt2105.security.repository

import ntnu.idatt2105.security.token.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID>
