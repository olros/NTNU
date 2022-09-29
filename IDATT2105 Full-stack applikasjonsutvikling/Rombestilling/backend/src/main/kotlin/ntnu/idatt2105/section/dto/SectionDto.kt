package ntnu.idatt2105.section.dto

import ntnu.idatt2105.core.util.SectionType
import ntnu.idatt2105.section.model.Section
import java.util.*

data class SectionDto(
    var id: UUID = UUID.randomUUID(),
    var name: String = "",
    var capacity: Int = 0,
    var description: String = "",
    var image: String = "",
    var type: String = SectionType.ROOM,
    var parent: SectionChildrenDto? = null,
    var children: List<SectionChildrenDto> = listOf(),
)

fun Section.toSectionDto() = SectionDto(
        id = this.id,
        name = this.name,
        capacity = this.capacity,
        description = this.description,
        image = this.image,
        type = this.getType(),
        parent = this.parent?.toSectionChildrenDto(),
        children = this.children.map { it.toSectionChildrenDto() }
)
