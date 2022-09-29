package ntnu.idatt2105.user.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvalidateExpiredUsersTasks(val userService: UserService) {

    @Scheduled(cron = "0 0 22 * * *")
    fun invalidateUsers() = userService.invalidateExpiredUsers()
}
