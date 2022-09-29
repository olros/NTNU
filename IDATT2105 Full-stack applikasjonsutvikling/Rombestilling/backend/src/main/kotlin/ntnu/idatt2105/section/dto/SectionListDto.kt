package ntnu.idatt2105.section.dto

import ntnu.idatt2105.core.util.SectionType
import ntnu.idatt2105.section.model.Section
import java.util.*

data class SectionListDto(
    var id: UUID? = null,
    var name: String = "",
    var capacity: Int = 0,
    var type: String = SectionType.ROOM,
    var parent: SectionChildrenDto? = null,
    var children: List<SectionChildrenDto> = listOf()
)

fun Section.toSectionListDto() = SectionListDto(
        id = this.id,
        name = this.name,
        capacity = this.capacity,
        type = this.getType(),
        parent = this.parent?.toSectionChildrenDto(),
        children = this.children.map { it.toSectionChildrenDto() }
)
