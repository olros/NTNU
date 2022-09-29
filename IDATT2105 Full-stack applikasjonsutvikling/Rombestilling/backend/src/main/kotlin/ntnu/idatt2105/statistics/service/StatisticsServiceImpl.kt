package ntnu.idatt2105.statistics.service

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Predicate
import ntnu.idatt2105.reservation.model.QReservation
import ntnu.idatt2105.reservation.model.Reservation
import ntnu.idatt2105.reservation.repository.ReservationRepository
import ntnu.idatt2105.section.repository.SectionRepository
import ntnu.idatt2105.statistics.dto.StatisticsDto
import org.apache.commons.collections4.IterableUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.util.*

@Service
class StatisticsServiceImpl(
    val sectionRepository: SectionRepository,
    val reservationRepository: ReservationRepository
) : StatisticsService {
    val logger = LoggerFactory.getLogger("StatisticsService")

    override fun getStatisticsForSection(sectionID: UUID, predicate: Predicate): StatisticsDto {
        val reservation = QReservation.reservation
        val newPredicate = ExpressionUtils.allOf(predicate, reservation.section.id.eq(sectionID))!!
        val reservations: Iterable<Reservation> = reservationRepository.findAll(newPredicate)

        logger.info("Getting statistics for sections")
        return StatisticsDto(
            nrOfReservation = getNrOfReservations(reservations),
            hoursOfReservation = getHoursOfReservation(reservations),
            daysWithReservation = getDaysWithReservation(reservations),
            userReservationCount = getuserReservationCount(reservations),
        )
    }

    private fun getuserReservationCount(reservations: Iterable<Reservation>): Int {
        return reservations.map { it.getEntityId() }.distinct().size
    }

    private fun getDaysWithReservation(reservations: Iterable<Reservation>): Int {
        val setOfDates = mutableSetOf<LocalDate>()
        reservations.forEach {
            setOfDates.add(it.fromTime!!.toLocalDate())
        }
        return setOfDates.size
    }

    private fun getNrOfReservations(reservations: Iterable<Reservation>): Int {
        return IterableUtils.size(reservations)
    }

    private fun getHoursOfReservation(reservations: Iterable<Reservation>): Long {
        return reservations.fold(0L) { acc, next -> acc + (Duration.between(next.fromTime, next.toTime)).toHours() }
    }
}
