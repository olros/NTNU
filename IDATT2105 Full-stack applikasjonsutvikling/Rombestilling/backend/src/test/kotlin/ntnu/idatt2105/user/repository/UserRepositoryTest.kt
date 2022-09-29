package ntnu.idatt2105.user.repository

import ntnu.idatt2105.factories.UserFactory
import ntnu.idatt2105.user.model.RoleType
import ntnu.idatt2105.user.model.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDate

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var nonExpiredUser: User

    private lateinit var expiredUser: User

    @BeforeEach
    internal fun setUp() {
        val userFactory = UserFactory()

        nonExpiredUser = userRepository.save(userFactory.`object`)

        expiredUser = userFactory.`object`
        expiredUser.expirationDate = LocalDate.now().minusDays(1)
        expiredUser = userRepository.save(expiredUser)
    }

    @Test
    fun `test that find by expiration date before and roles name returns all expired users with user roles`() {
        val actualUsers =
            userRepository.findByExpirationDateBeforeAndRolesName(LocalDate.now(), RoleType.USER)

        assertThat(actualUsers).containsOnly(expiredUser)
    }
}
