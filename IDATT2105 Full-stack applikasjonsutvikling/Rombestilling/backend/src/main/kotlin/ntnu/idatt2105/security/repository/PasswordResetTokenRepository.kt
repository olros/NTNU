package ntnu.idatt2105.security.repository

import ntnu.idatt2105.security.token.PasswordResetToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, UUID>
