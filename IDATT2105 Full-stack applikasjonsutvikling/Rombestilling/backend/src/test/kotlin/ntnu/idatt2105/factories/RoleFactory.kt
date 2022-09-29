package ntnu.idatt2105.factories

import ntnu.idatt2105.user.model.Role
import ntnu.idatt2105.user.model.RoleType
import org.springframework.beans.factory.FactoryBean
import java.util.*

class RoleFactory : FactoryBean<Role> {

    fun getObject(name: String = RoleType.USER) = Role(id = UUID.randomUUID(), name = name)

    override fun getObject(): Role = Role(id = UUID.randomUUID(), RoleType.USER)

    override fun getObjectType(): Class<*> = Role::class.java
}
