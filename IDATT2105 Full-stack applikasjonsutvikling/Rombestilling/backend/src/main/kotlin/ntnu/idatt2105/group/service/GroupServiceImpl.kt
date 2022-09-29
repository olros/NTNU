package ntnu.idatt2105.group.service

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Predicate
import ntnu.idatt2105.core.exception.ApplicationException
import ntnu.idatt2105.core.exception.EntityType
import ntnu.idatt2105.core.exception.ExceptionType
import ntnu.idatt2105.group.dto.CreateGroupDto
import ntnu.idatt2105.group.dto.GroupDto
import ntnu.idatt2105.group.dto.toGroupDto
import ntnu.idatt2105.group.model.Group
import ntnu.idatt2105.group.model.QGroup
import ntnu.idatt2105.group.repository.GroupRepository
import ntnu.idatt2105.user.model.User
import ntnu.idatt2105.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class GroupServiceImpl(val groupRepository: GroupRepository, val userService: UserService) : GroupService {

    @Transactional
    override fun createGroup(group: CreateGroupDto, creatorId: UUID): GroupDto {
        val newGroup = Group(id = UUID.randomUUID(), members = mutableSetOf(), creator = userService.getUser(creatorId, User::class.java), name = group.name)
        return groupRepository.save(newGroup).toGroupDto()
    }

    @Transactional
    override fun updateGroup(groupId: UUID, group: Group): GroupDto {
        getGroupById(groupId).run {
            val updatedGroup = this.copy(
                    name = group.name
            )
            return groupRepository.save(updatedGroup).toGroupDto()
        }
    }

    @Transactional
    override fun deleteGroup(groupId: UUID) {
        getGroupById(groupId).run {
            groupRepository.delete(this)
        }
    }

    override fun getGroup(groupId: UUID): GroupDto {
        return getGroupById(groupId)
            .toGroupDto()
    }

    override fun getAllGroups(pageable: Pageable, predicate: Predicate, creatorId: UUID): Page<GroupDto> {
        val group = QGroup.group
        val user = userService.getUser(creatorId, User::class.java)
        val newPredicate = if (user.isAdmin()) predicate else ExpressionUtils.allOf(predicate, group.creator.id.eq(creatorId).or(group.members.any().id.eq(creatorId)))!!
        return groupRepository.findAll(newPredicate, pageable).map {
            it.toGroupDto()
        }
    }

    override fun getUserGroups(userId: UUID): List<GroupDto> {
        return groupRepository.findAllByMembers_IdOrCreator_id(userId, userId).map { it.toGroupDto() }
    }

    private fun getGroupById(id: UUID): Group = groupRepository.findById(id).orElseThrow { throw ApplicationException.throwException(
                EntityType.GROUP, ExceptionType.ENTITY_NOT_FOUND, id.toString()) }

    override fun getReserverById(id: UUID): Group = getGroupById(id)
}
