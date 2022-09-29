package ntnu.idatt2105.section.model

import com.querydsl.core.annotations.PropertyType
import com.querydsl.core.annotations.QueryType
import ntnu.idatt2105.core.util.SectionType
import ntnu.idatt2105.reservation.model.GroupReservation
import ntnu.idatt2105.reservation.model.UserReservation
import org.springframework.format.annotation.DateTimeFormat
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.*

@Entity
data class Section(
    @Id
    @Column(columnDefinition = "CHAR(32)")
    var id: UUID = UUID.randomUUID(),
    var name: String = "",
    @Column(columnDefinition = "TEXT")
    var description: String = "",
    var capacity: Int = 0,
    var image: String,
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent", cascade = [CascadeType.ALL])
    var children: MutableList<Section> = mutableListOf(),
    @ManyToOne
    var parent: Section? = null,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "section")
    var userReservation: MutableList<UserReservation> = mutableListOf(),
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "section")
    var groupReservation: MutableList<GroupReservation> = mutableListOf()
) {
        fun getType(): String {
                if (parent != null)
                        return SectionType.SECTION
                return SectionType.ROOM
        }

    fun isRoom() = getType() == SectionType.ROOM

    fun hasNoCapacityFor(section: Section): Boolean = getPreoccupationDegree() + section.capacity > this.capacity

    private fun getPreoccupationDegree(): Int = children.fold(0, { acc, next -> acc + next.capacity })

    override fun toString(): String {
        return "Section(id=$id, name='$name', description='$description', capacity=$capacity, image='$image', children=$children, reservation=$userReservation)"
    }

    @Transient
    @QueryType(PropertyType.DATETIME)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var interval: ZonedDateTime? = null
}
