package ntnu.idatt2105.user.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Role(
    @Id
    @Column(columnDefinition = "CHAR(32)")
    val id: UUID = UUID.randomUUID(),
    val name: String
)
