package ntnu.idatt2105.group.controller

import com.querydsl.core.types.Predicate
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.group.service.MembershipService
import ntnu.idatt2105.user.dto.UserEmailDto
import ntnu.idatt2105.user.dto.UserListDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
class MembershipControllerImpl(val membershipService: MembershipService) : MembershipController {
    override fun getMemberships(predicate: Predicate, pageable: Pageable, groupId: UUID): Page<UserListDto> =
            membershipService.getMemberships(groupId, predicate, pageable)

    override fun createMembership(predicate: Predicate, pageable: Pageable, groupId: UUID, userEmail: UserEmailDto): Page<UserListDto> =
            membershipService.createMemberships(groupId, userEmail, predicate, pageable)

    override fun createMembershipBatch(
        groupId: UUID,
        file: MultipartFile
    ): Response = membershipService.createMembershipBatch(file, groupId)

    override fun deleteMembership(groupId: UUID, userId: UUID): ResponseEntity<Response> {
        membershipService.deleteMembership(groupId, userId)
        return ResponseEntity(Response("Membership has been deleted"), HttpStatus.OK)
    }
}
