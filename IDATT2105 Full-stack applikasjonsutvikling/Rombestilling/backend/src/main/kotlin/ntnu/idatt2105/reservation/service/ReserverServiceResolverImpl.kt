package ntnu.idatt2105.reservation.service

import ntnu.idatt2105.group.service.GroupService
import ntnu.idatt2105.user.service.UserService
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

@Service
class ReserverServiceResolverImpl(
    private val groupService: GroupService,
    private val userService: UserService
) : ReserverServiceResolver {

    override fun resolveService(reservationType: String?): ReserverService =
        when (reservationType) {
            "user" -> userService
            "group" -> groupService
            else -> throw IllegalArgumentException("Unsupported reserver service for type $reservationType")
        }
}
