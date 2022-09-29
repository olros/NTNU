package ntnu.idatt2105.reservation.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import ntnu.idatt2105.reservation.model.Reservation
import ntnu.idatt2105.section.dto.SectionChildrenDto
import java.time.ZonedDateTime
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = ReservationUserDto::class, name = "user"),
        JsonSubTypes.Type(value = GroupReservationDto::class, name = "group"))
abstract class ReservationDto(
    open val id: UUID? = null,
    open val fromTime: ZonedDateTime? = null,
    open val toTime: ZonedDateTime? = null,
    open val text: String = "",
    open val nrOfPeople: Int = -1,
    open val section: SectionChildrenDto? = null,
) {
    abstract fun getEntityId(): UUID?
    abstract fun getType(): String
    abstract fun toReservation(): Reservation
}
