package ntnu.idatt2105.security.service

import ntnu.idatt2105.core.exception.ApplicationException
import ntnu.idatt2105.core.exception.EntityType
import ntnu.idatt2105.core.exception.ExceptionType
import ntnu.idatt2105.group.repository.GroupRepository
import ntnu.idatt2105.reservation.model.GroupReservation
import ntnu.idatt2105.reservation.model.Reservation
import ntnu.idatt2105.reservation.repository.ReservationRepository
import ntnu.idatt2105.user.model.User
import ntnu.idatt2105.user.repository.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class SecurityService(
    val userRepository: UserRepository,
    val groupRepository: GroupRepository,
    val reservationRepository: ReservationRepository
) {

    private fun getUser(): User? {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        val userDetails = authentication?.principal as UserDetails
        val email = userDetails.username
        return userRepository.findByEmail(email)
    }

    fun reservationPermissions(reservationId: UUID): Boolean {
        val user = getUser() ?: return false
        if (user.isAdmin()) {
            return true
        }
        val reservation: Reservation? = reservationRepository.findById(reservationId).orElse(null)
        return isOwnReservation(reservation, user) || isMemberOfGroupReservation(reservation)
    }

    private fun isOwnReservation(
        reservation: Reservation?,
        user: User
    ) = reservation?.getEntityId() == user.id

    private fun isMemberOfGroupReservation(reservation: Reservation?) =
        (reservation as? GroupReservation)?.group?.isMember() == true

    /**
     * Check if a user has group permissions
     * @param groupId Id of group
     * @param onlyRead Only check if user has read-access
     */
    fun groupPermissions(groupId: UUID, onlyRead: Boolean): Boolean {
        val user = getUser() ?: return false
        return if (user.isAdmin()) {
            true
        } else {
            val group = groupRepository.findById(groupId).orElseThrow {
                throw ApplicationException.throwException(
                    EntityType.GROUP, ExceptionType.ENTITY_NOT_FOUND, groupId.toString()
                )
            }
            return if (group != null) {
                group.creator == user || (onlyRead && group.members.contains(user))
            } else false
        }
    }
}
