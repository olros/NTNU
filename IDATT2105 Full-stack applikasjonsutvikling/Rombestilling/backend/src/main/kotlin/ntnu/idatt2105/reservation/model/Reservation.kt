package ntnu.idatt2105.reservation.model

import com.querydsl.core.annotations.PropertyType
import com.querydsl.core.annotations.QueryType
import ntnu.idatt2105.reservation.dto.ReservationDto
import ntnu.idatt2105.section.model.Section
import org.springframework.format.annotation.DateTimeFormat
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.*

interface Reserver

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
abstract class Reservation(
    @Id
@Column(columnDefinition = "CHAR(32)")
open var id: UUID = UUID.randomUUID(),
    @ManyToOne
    @JoinColumn(name = "section_id", referencedColumnName = "id")
           open var section: Section? = null,
    open var fromTime: ZonedDateTime? = null,
    open var toTime: ZonedDateTime? = null,
    open var text: String = "",
    open var nrOfPeople: Int = 1,
) {

    abstract fun setRelation(entity: Reserver)
    abstract fun getEntityId(): UUID?
    abstract fun toReservationDto(): ReservationDto

    @Transient
    @QueryType(PropertyType.DATETIME)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var fromTimeAfter: ZonedDateTime? = null

    @Transient
    @QueryType(PropertyType.DATETIME)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var toTimeBefore: ZonedDateTime? = null
}
