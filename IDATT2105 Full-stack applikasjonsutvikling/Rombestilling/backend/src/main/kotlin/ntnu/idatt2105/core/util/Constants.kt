package ntnu.idatt2105.core.util

import java.time.LocalTime

object PaginationConstants {
    const val PAGINATION_SIZE = 25
}

object SectionType {
    const val SECTION = "section"
    const val ROOM = "room"
}

object SecurityConstants {
    const val AUTHORITIES_KEY = "scopes"
}

object ReservationConstants {
    const val MAX_DURATION = 14
    const val MAX_MONTHS_FOR_USER_RESERVING_IN_FUTURE = 1L
    const val MAX_MONTHS_FOR_ADMIN_RESERVING_IN_FUTURE = 6L
    val EARLIEST_RESERVATION_TIME_OF_DAY: LocalTime = LocalTime.of(3, 59) // Allow exactly 06:00
    val LATEST_RESERVATION_TIME_OF_DAY: LocalTime = LocalTime.of(18, 1) // Allow exactly 20:00
}
