package ntnu.idatt2105.reservation.validation

import ntnu.idatt2105.core.util.ReservationConstants
import ntnu.idatt2105.reservation.dto.ReservationCreateDto
import ntnu.idatt2105.user.model.RoleType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import java.time.ZonedDateTime
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class ReservationAllowedFromDateTimeValidator :
    ConstraintValidator<ReservationAllowedFromDateTime?, ReservationCreateDto> {

    override fun initialize(constraintAnnotation: ReservationAllowedFromDateTime?) {
    }

    override fun isValid(reservation: ReservationCreateDto, constraintValidatorContext: ConstraintValidatorContext): Boolean {

        val isInTheFuture = ZonedDateTime.now().isBefore(reservation.fromTime)
        val isStartingAfterEarliestTimeOfDay = reservation.fromTime?.isAfter(
            reservation.fromTime?.with(ReservationConstants.EARLIEST_RESERVATION_TIME_OF_DAY)) ?: false
        val isStartingBeforeLatestTimeOfDay = reservation.fromTime?.isBefore(
            reservation.fromTime?.with(ReservationConstants.LATEST_RESERVATION_TIME_OF_DAY)) ?: false
        val isWithinMaxMonthsInTheFuture = isWithinMaxMonthsInTheFuture(reservation)

        return isInTheFuture &&
            isWithinMaxMonthsInTheFuture &&
            isStartingAfterEarliestTimeOfDay &&
            isStartingBeforeLatestTimeOfDay
    }

    private fun isWithinMaxMonthsInTheFuture(reservation: ReservationCreateDto): Boolean {
        var maxMonthsForFutureReservations = ReservationConstants.MAX_MONTHS_FOR_USER_RESERVING_IN_FUTURE

        val auth: Authentication = SecurityContextHolder.getContext().authentication
        if (auth.authorities.any { it?.authority == RoleType.ADMIN })
            maxMonthsForFutureReservations = ReservationConstants.MAX_MONTHS_FOR_ADMIN_RESERVING_IN_FUTURE

        return reservation.fromTime?.isBefore(
            ZonedDateTime.now().plusMonths(maxMonthsForFutureReservations)
        ) ?: false
    }
}
