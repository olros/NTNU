package ntnu.idatt2105.factories

import io.github.serpro69.kfaker.Faker
import ntnu.idatt2105.group.model.Group
import org.springframework.beans.factory.FactoryBean
import java.util.*

class GroupFactory : FactoryBean<Group> {

    private val faker = Faker()

    override fun getObjectType(): Class<*> =
         Group::class.java

    override fun isSingleton(): Boolean = false

    override fun getObject(): Group =
         Group(
                id = UUID.randomUUID(),
                name = faker.beer.yeast(),
                members = mutableSetOf(),
                 creator = UserFactory().`object`
        )
}
