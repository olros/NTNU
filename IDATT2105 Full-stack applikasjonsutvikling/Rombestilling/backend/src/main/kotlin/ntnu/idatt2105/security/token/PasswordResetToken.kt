package ntnu.idatt2105.security.token

import ntnu.idatt2105.user.model.User
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.*

@Entity
data class PasswordResetToken(
    @Id @Column(columnDefinition = "CHAR(32)")
    var id: UUID = UUID.randomUUID(),
    @OneToOne
    @JoinColumn(nullable = true, referencedColumnName = "id")
    val user: User,
    val expirationDate: ZonedDateTime = ZonedDateTime.now().plusMinutes(EXPIRATION.toLong())
) {

    companion object {
        // 60 minutes
        private const val EXPIRATION = 60
    }
}
fun PasswordResetToken.isAfter() = this.expirationDate.isAfter(ZonedDateTime.now())
