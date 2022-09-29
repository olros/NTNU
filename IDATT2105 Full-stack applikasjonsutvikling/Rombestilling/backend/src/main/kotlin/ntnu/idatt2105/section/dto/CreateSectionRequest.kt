package ntnu.idatt2105.section.dto

import ntnu.idatt2105.section.model.Section
import java.util.*
import javax.validation.constraints.Positive

data class CreateSectionRequest(
    var id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    @get:Positive(message = "Section capacity must be positive")
    var capacity: Int = 1,
    var image: String = "",
    var parentId: UUID? = null
)

fun CreateSectionRequest.toSection() = Section(
    id = this.id,
    name = this.name,
    description = this.description,
    capacity = this.capacity,
    image = this.image,
)
