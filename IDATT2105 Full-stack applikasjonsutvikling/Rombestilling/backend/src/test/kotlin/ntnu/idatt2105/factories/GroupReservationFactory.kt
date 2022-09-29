package ntnu.idatt2105.factories

import io.github.serpro69.kfaker.Faker
import ntnu.idatt2105.core.util.ReservationConstants
import ntnu.idatt2105.reservation.model.GroupReservation
import ntnu.idatt2105.reservation.model.Reservation
import org.springframework.beans.factory.FactoryBean
import java.time.ZonedDateTime
import java.util.*

class GroupReservationFactory : FactoryBean<GroupReservation> {

    val faker = Faker()
    val groupFactory = GroupFactory()
    val sectionFactory = SectionFactory()

    override fun getObjectType(): Class<*> {
        return Reservation::class.java
    }

    override fun isSingleton(): Boolean {
        return false
    }

    override fun getObject(): GroupReservation {
        val group = groupFactory.`object`
        val section = sectionFactory.`object`
        val tomorrow = ZonedDateTime.now().plusDays(1)
        val earliestStartTimeTomorrow = tomorrow.with(ReservationConstants.EARLIEST_RESERVATION_TIME_OF_DAY)

        return GroupReservation(
                id = UUID.randomUUID(),
                group = group,
                section = section,
                fromTime = earliestStartTimeTomorrow.plusHours(1),
                toTime = earliestStartTimeTomorrow.plusHours(5),
                text = faker.bojackHorseman.characters(),
                nrOfPeople = (0..10000).random())
    }
}
