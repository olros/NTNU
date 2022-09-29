package ntnu.idatt2105.reservation.dto

import com.fasterxml.jackson.annotation.JsonTypeName
import ntnu.idatt2105.group.model.Group
import ntnu.idatt2105.reservation.model.GroupReservation
import ntnu.idatt2105.reservation.model.Reservation
import ntnu.idatt2105.section.dto.SectionChildrenDto
import java.time.ZonedDateTime
import java.util.*

@JsonTypeName("group")
data class GroupReservationDto(
    override val id: UUID? = null,
    override val fromTime: ZonedDateTime? = null,
    override val toTime: ZonedDateTime? = null,
    override val text: String = "",
    override val nrOfPeople: Int = -1,
    override val section: SectionChildrenDto? = null,
    val group: Group? = null
) : ReservationDto(id, fromTime, toTime, text, nrOfPeople, section) {

    override fun getEntityId(): UUID? = group?.id
    override fun getType(): String = "group"

    override fun toReservation(): Reservation = GroupReservation(id = this.id!!, toTime = this.toTime, fromTime = this.fromTime, text = this.text, nrOfPeople = this.nrOfPeople)
}
