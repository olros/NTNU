package ntnu.idatt2105.reservation.validation

import javax.validation.Constraint
import kotlin.reflect.KClass

@Target(AnnotationTarget.TYPE, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ReservationMaximumDurationValidator::class])
annotation class ReservationMaximumDuration(
    val message: String = "Reservation duration must be less than 14 hours",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Any>> = []
)
