package ntnu.idatt2105.factories

import io.github.serpro69.kfaker.Faker
import ntnu.idatt2105.core.util.ReservationConstants
import ntnu.idatt2105.reservation.model.Reservation
import ntnu.idatt2105.reservation.model.UserReservation
import org.springframework.beans.factory.FactoryBean
import java.time.ZonedDateTime
import java.util.*

class UserReservationFactory : FactoryBean<UserReservation> {

    val faker = Faker()
    val userFactory = UserFactory()
    val sectionFactory = SectionFactory()

    override fun getObjectType(): Class<*> {
        return Reservation::class.java
    }

    override fun isSingleton(): Boolean {
        return false
    }

    override fun getObject(): UserReservation {
        val user = userFactory.`object`
        val section = sectionFactory.`object`
        val tomorrow = ZonedDateTime.now().plusDays(1)
        val earliestStartTimeTomorrow = tomorrow.with(ReservationConstants.EARLIEST_RESERVATION_TIME_OF_DAY)

        return UserReservation(
                id = UUID.randomUUID(),
                user = user,
               section = section,
                fromTime = earliestStartTimeTomorrow.plusHours(1),
                toTime = earliestStartTimeTomorrow.plusHours(5),
                text = faker.bojackHorseman.characters(),
                nrOfPeople = (0..10000).random())
    }
}
