package ntnu.idatt2105.reservation.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import ntnu.idatt2105.reservation.model.Reservation
import ntnu.idatt2105.reservation.validation.ReservationAllowedFromDateTime
import ntnu.idatt2105.reservation.validation.ReservationFromDateTimeBeforeToDateTime
import ntnu.idatt2105.reservation.validation.ReservationMaximumDuration
import java.time.ZonedDateTime
import java.util.*
import javax.validation.constraints.Positive

@ReservationMaximumDuration
@ReservationAllowedFromDateTime
@ReservationFromDateTimeBeforeToDateTime
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        Type(value = CreateUserReservationRequest::class, name = "user"),
        Type(value = CreateGroupReservationRequest::class, name = "group"))
abstract class ReservationCreateDto(
    open val sectionId: UUID? = null,
    open val fromTime: ZonedDateTime? = null,
    open val toTime: ZonedDateTime? = null,
    open val text: String = "",
    @get:Positive(message = "The number of people must be positive")
    open val nrOfPeople: Int = 1,
    open val entityId: UUID? = null,
    open val type: String? = null
) {

        abstract fun toReservation(): Reservation
}
