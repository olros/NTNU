package ntnu.idatt2105.reservation.validation

import javax.validation.Constraint
import kotlin.reflect.KClass

@Target(AnnotationTarget.TYPE, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ReservationFromDateTimeBeforeToDateTimeValidator::class])
annotation class ReservationFromDateTimeBeforeToDateTime(
    val message: String = "Reservation start time must be before end time",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Any>> = []
)
