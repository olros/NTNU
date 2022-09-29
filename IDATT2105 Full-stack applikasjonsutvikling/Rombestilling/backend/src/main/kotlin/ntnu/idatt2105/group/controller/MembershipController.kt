package ntnu.idatt2105.group.controller

import com.querydsl.core.types.Predicate
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.core.util.PaginationConstants
import ntnu.idatt2105.user.dto.UserEmailDto
import ntnu.idatt2105.user.dto.UserListDto
import ntnu.idatt2105.user.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.querydsl.binding.QuerydslPredicate
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Api(value = "Group services", tags = ["Group Services"], description = "Group Services")
@RequestMapping("/groups/{groupId}/memberships/")
@PreAuthorize("@securityService.groupPermissions(#groupId, false)")
interface MembershipController {

    @Operation(summary = "Fetch memberships for the given group", responses = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "404", description = "Not found: new group does not exist"),

    ])
    @GetMapping
    @PreAuthorize("@securityService.groupPermissions(#groupId, true)")
    fun getMemberships(
        @QuerydslPredicate(root = User::class) predicate: Predicate,
        @PageableDefault(size = PaginationConstants.PAGINATION_SIZE,
        sort = ["firstName"], direction = Sort.Direction.DESC) pageable: Pageable,
        @PathVariable groupId: UUID
    ): Page<UserListDto>

    @Operation(summary = "Create a new membership", responses = [
        ApiResponse(responseCode = "201", description = "Created: new membership was created"),
        ApiResponse(responseCode = "404", description = "Not found: new group or user does not exist"),
    ])
    @PostMapping
    fun createMembership(
        @QuerydslPredicate(root = User::class) predicate: Predicate,
        @PageableDefault(size = PaginationConstants.PAGINATION_SIZE,
        sort = ["firstName"], direction = Sort.Direction.DESC) pageable: Pageable,
        @PathVariable groupId: UUID,
        @RequestBody userEmail: UserEmailDto
    ): Page<UserListDto>

    @Operation(summary = "Create a batch of memberships", responses = [
        ApiResponse(responseCode = "201", description = "Created: new memberships was created"),
        ApiResponse(responseCode = "404", description = "Not found: new group or user does not exist")
    ])
    @PostMapping("batch-memberships/")
    fun createMembershipBatch(
        @PathVariable groupId: UUID,
        @RequestParam("file") file: MultipartFile
    ): Response

    @Operation(summary = "Delete a membership", responses = [
        ApiResponse(responseCode = "200", description = "OK: membership was deleted"),
        ApiResponse(responseCode = "404", description = "Not found: new group or user does not exist"),
    ])
    @DeleteMapping("{userId}/")
    fun deleteMembership(@PathVariable groupId: UUID, @PathVariable userId: UUID): ResponseEntity<Response>
}
