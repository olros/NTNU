package ntnu.idatt2105.factories

import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.FakerConfig
import io.github.serpro69.kfaker.create
import ntnu.idatt2105.user.model.RoleType
import ntnu.idatt2105.user.model.User
import ntnu.idatt2105.user.model.UserBuilder
import org.springframework.beans.factory.FactoryBean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.lang.Exception
import java.time.LocalDate
import java.util.*

class UserFactory : FactoryBean<User> {

    private val config = FakerConfig.builder().create {
        uniqueGeneratorRetryLimit = 10000
    }

    private val faker = Faker(config)

    private val encoder = BCryptPasswordEncoder()

    @Throws(Exception::class)
    override fun getObject(): User {
        faker.unique.clearAll()
        faker.unique.configuration { enable(faker::internet) }
        faker.unique.configuration { enable(faker::name) }
        val userRole = RoleFactory().`object`

        return UserBuilder(
            id = UUID.randomUUID(),
            email = faker.internet.email(),
            firstName = faker.name.firstName(),
            surname = faker.name.lastName(),
            password = encoder.encode(faker.rickAndMorty.locations()),
            phoneNumber = faker.phoneNumber.phoneNumber(),
            expirationDate = LocalDate.now().plusDays(1),
            roles = mutableSetOf(userRole)
        ).build()
    }

    fun admin(): User =
        this.`object`.copy(roles = mutableSetOf(
            RoleFactory().`object`,
            RoleFactory().getObject(RoleType.ADMIN)
        ))

    override fun getObjectType(): Class<*>? {
        return null
    }

    override fun isSingleton(): Boolean {
        return false
    }
}
