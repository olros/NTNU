package ntnu.idatt2105.group.controller

import com.querydsl.core.types.Predicate
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.group.dto.CreateGroupDto
import ntnu.idatt2105.group.dto.GroupDto
import ntnu.idatt2105.group.model.Group
import ntnu.idatt2105.group.service.GroupService
import ntnu.idatt2105.reservation.service.ReservationService
import ntnu.idatt2105.user.service.UserDetailsImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class GroupControllerImpl(val groupService: GroupService, val reservationService: ReservationService) : GroupController {
    override fun getAllGroups(predicate: Predicate, pageable: Pageable, principal: UserDetailsImpl) = groupService.getAllGroups(pageable, predicate, principal.getId())

    override fun getGroup(groupId: UUID): ResponseEntity<GroupDto> =
            ResponseEntity(groupService.getGroup(groupId), HttpStatus.OK)

    override fun getGroupReservations(
        predicate: Predicate,
        pageable: Pageable,
        groupId: UUID
    ) = reservationService.getGroupReservation(groupId, pageable, predicate)

    override fun createGroup(group: CreateGroupDto, principal: UserDetailsImpl): ResponseEntity<GroupDto> =
            ResponseEntity(groupService.createGroup(group, principal.getId()), HttpStatus.CREATED)

    override fun updateGroup(groupId: UUID, group: Group): ResponseEntity<GroupDto> =
            ResponseEntity(groupService.updateGroup(groupId, group), HttpStatus.OK)

    override fun deleteGroup(groupId: UUID): ResponseEntity<Response> {
        groupService.deleteGroup(groupId)
        return ResponseEntity(Response("Group was deleted"), HttpStatus.OK)
    }
}
