package ntnu.idatt2105.group.controller

import com.querydsl.core.types.Predicate
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.core.util.PaginationConstants
import ntnu.idatt2105.group.dto.CreateGroupDto
import ntnu.idatt2105.group.dto.GroupDto
import ntnu.idatt2105.group.model.Group
import ntnu.idatt2105.reservation.dto.ReservationDto
import ntnu.idatt2105.reservation.model.Reservation
import ntnu.idatt2105.user.service.UserDetailsImpl
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.querydsl.binding.QuerydslPredicate
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@Api(value = "Group services", tags = ["Group Services"], description = "Group Services")
@RequestMapping("/groups/")
interface GroupController {

    @Operation(summary = "Fetch all groups", responses = [
        ApiResponse(responseCode = "200", description = "Success"),
    ])
    @GetMapping
    fun getAllGroups(
        @QuerydslPredicate(root = Group::class) predicate: Predicate,
        @PageableDefault(size = PaginationConstants.PAGINATION_SIZE,
                sort = ["name"], direction = Sort.Direction.DESC) pageable: Pageable,
        @AuthenticationPrincipal principal: UserDetailsImpl
    ): Page<GroupDto>

    @Operation(summary = "Fetch group details for the given group id", responses = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "404", description = "Not found: group with the given id does not exist")
    ])
    @GetMapping("{groupId}/")
    @PreAuthorize("@securityService.groupPermissions(#groupId, true)")
    fun getGroup(@PathVariable groupId: UUID): ResponseEntity<GroupDto>

    @Operation(summary = "Fetch reservations for a given group", responses = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "404", description = "Not found: group with the given id does not exist")
    ])
    @GetMapping("{groupId}/reservations/")
    fun getGroupReservations(
        @QuerydslPredicate(root = Reservation::class) predicate: Predicate,
        @PageableDefault(size = PaginationConstants.PAGINATION_SIZE, sort = ["fromTime"], direction = Sort.Direction.ASC) pageable: Pageable,
        @PathVariable groupId: UUID
    ): Page<ReservationDto>

    @Operation(summary = "Create a new group", responses = [
        ApiResponse(responseCode = "201", description = "Created: new group was created"),
    ])
    @PostMapping
    fun createGroup(@RequestBody group: CreateGroupDto, @AuthenticationPrincipal principal: UserDetailsImpl): ResponseEntity<GroupDto>

    @Operation(summary = "Update existing group", responses = [
        ApiResponse(responseCode = "200", description = "Success: group was updated"),
        ApiResponse(responseCode = "404", description = "Not found: group with the given id does not exist"),
    ])
    @PutMapping("{groupId}/")
    @PreAuthorize("@securityService.groupPermissions(#groupId, false)")
    fun updateGroup(@PathVariable groupId: UUID, @RequestBody group: Group): ResponseEntity<GroupDto>

    @Operation(summary = "Delete existing group", responses = [
        ApiResponse(responseCode = "200", description = "Success: group was deleted"),
        ApiResponse(responseCode = "404", description = "Not found: group with the given id does not exist"),
    ])
    @DeleteMapping("{groupId}/")
    @PreAuthorize("@securityService.groupPermissions(#groupId, false)")
    fun deleteGroup(@PathVariable groupId: UUID): ResponseEntity<Response>
}
