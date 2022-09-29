package ntnu.idatt2105.reservation.validation

import ntnu.idatt2105.reservation.dto.ReservationCreateDto
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class ReservationFromDateTimeBeforeToDateTimeValidator :
    ConstraintValidator<ReservationFromDateTimeBeforeToDateTime?, ReservationCreateDto> {

    override fun initialize(constraintAnnotation: ReservationFromDateTimeBeforeToDateTime?) {
    }

    override fun isValid(reservation: ReservationCreateDto, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        return reservation.fromTime?.isBefore(reservation.toTime) ?: false
    }
}
