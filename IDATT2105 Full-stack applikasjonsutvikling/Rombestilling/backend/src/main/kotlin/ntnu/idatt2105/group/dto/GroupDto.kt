package ntnu.idatt2105.group.dto

import ntnu.idatt2105.group.model.Group
import ntnu.idatt2105.user.dto.UserListDto
import ntnu.idatt2105.user.dto.toUserListDto
import java.util.*

data class GroupDto(
    var id: UUID,
    var name: String = "",
    var creator: UserListDto,
    var isMember: Boolean = false,
)
fun Group.toGroupDto() = GroupDto(
        id = this.id,
        name = this.name,
        creator = this.creator.toUserListDto(),
        isMember = this.isMember()
)
