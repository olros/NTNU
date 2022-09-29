package ntnu.idatt2105.user.controller

import com.querydsl.core.types.Predicate
import ntnu.idatt2105.group.dto.GroupDto
import ntnu.idatt2105.group.service.GroupService
import ntnu.idatt2105.reservation.service.ReservationService
import ntnu.idatt2105.user.dto.DetailedUserDto
import ntnu.idatt2105.user.dto.UserDto
import ntnu.idatt2105.user.dto.UserRegistrationDto
import ntnu.idatt2105.user.service.UserDetailsImpl
import ntnu.idatt2105.user.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
class UserControllerImpl(val userService: UserService, val reservationService: ReservationService, val groupService: GroupService) : UserController {

    override fun registerUser(userRegistrationDto: UserRegistrationDto): ResponseEntity<UserDto> =
            ResponseEntity(userService.registerUser(userRegistrationDto), HttpStatus.CREATED)

    override fun registerUserBatch(file: MultipartFile) =
        ResponseEntity(userService.registerUserBatch(file), HttpStatus.CREATED)

    override fun getMe(principal: UserDetailsImpl) =
            ResponseEntity.ok(userService.getUser(principal.getId(), mapTo = DetailedUserDto::class.java))

    override fun getUser(userId: UUID) =
        ResponseEntity.ok(userService.getUser(userId, mapTo = DetailedUserDto::class.java))

    override fun getUsers(
        predicate: Predicate,
        pageable: Pageable
    ) =
        ResponseEntity(userService.getUsers(pageable, predicate), HttpStatus.OK)

    override fun updateUser(userId: UUID, user: UserDto) =
            ResponseEntity.ok(userService.updateUser(userId, user))

    override fun deleteUser(userId: UUID) =
        ResponseEntity.ok(userService.deleteUser(userId))

    override fun getMyReservations(
        predicate: Predicate,
        pageable: Pageable,
        principal: UserDetailsImpl
    ) =
        reservationService.getUserReservation(principal.getId(), pageable, predicate)

    override fun getuserReservations(
        userId: UUID,
        predicate: Predicate,
        pageable: Pageable,
    ) =
        reservationService.getUserReservation(userId, pageable, predicate)

    override fun getUserGroups(principal: UserDetailsImpl): List<GroupDto> {
        return groupService.getUserGroups(principal.getId())
    }
}
