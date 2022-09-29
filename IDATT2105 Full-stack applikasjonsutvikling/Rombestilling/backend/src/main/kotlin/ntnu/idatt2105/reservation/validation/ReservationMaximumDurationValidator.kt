package ntnu.idatt2105.reservation.validation

import ntnu.idatt2105.core.util.ReservationConstants
import ntnu.idatt2105.reservation.dto.ReservationCreateDto
import java.time.Duration
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class ReservationMaximumDurationValidator :
    ConstraintValidator<ReservationMaximumDuration?, ReservationCreateDto> {

    override fun initialize(constraintAnnotation: ReservationMaximumDuration?) {
    }

    override fun isValid(reservation: ReservationCreateDto, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        val isMax14Hours = Duration.between(reservation.fromTime, reservation.toTime).toHours() <= ReservationConstants.MAX_DURATION
        return isMax14Hours
    }
}
