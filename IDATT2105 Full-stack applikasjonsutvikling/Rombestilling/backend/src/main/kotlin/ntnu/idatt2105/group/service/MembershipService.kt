package ntnu.idatt2105.group.service

import com.querydsl.core.types.Predicate
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.user.dto.UserEmailDto
import ntnu.idatt2105.user.dto.UserListDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface MembershipService {
    fun getMemberships(groupId: UUID, predicate: Predicate, pageable: Pageable): Page<UserListDto>
    fun createMemberships(groupId: UUID, userEmail: UserEmailDto, predicate: Predicate, pageable: Pageable): Page<UserListDto>
    fun deleteMembership(groupId: UUID, userId: UUID)
    fun createMembershipBatch(file: MultipartFile, groupId: UUID): Response
}
