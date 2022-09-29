package ntnu.idatt2105.reservation.validation

import javax.validation.Constraint
import kotlin.reflect.KClass

@Target(AnnotationTarget.TYPE, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ReservationAllowedFromDateTimeValidator::class])
annotation class ReservationAllowedFromDateTime(
    val message: String = "Reservation start date must be from now to up to 1 month ahead and start between 06:00 and 20:00",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Any>> = []
)
