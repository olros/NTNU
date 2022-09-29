package ntnu.idatt2105.reservation.service

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Predicate
import ntnu.idatt2105.core.exception.ApplicationException
import ntnu.idatt2105.core.exception.EntityType
import ntnu.idatt2105.core.exception.ExceptionType
import ntnu.idatt2105.reservation.dto.ReservationCreateDto
import ntnu.idatt2105.reservation.dto.ReservationDto
import ntnu.idatt2105.reservation.model.QGroupReservation
import ntnu.idatt2105.reservation.model.QReservation
import ntnu.idatt2105.reservation.model.QUserReservation
import ntnu.idatt2105.reservation.repository.ReservationRepository
import ntnu.idatt2105.section.model.QSection
import ntnu.idatt2105.section.repository.SectionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ReservationServiceImpl(
    val reservationRepository: ReservationRepository,
    val sectionRepository: SectionRepository,
    val reserverServiceResolver: ReserverServiceResolver
) : ReservationService {

    lateinit var reserverService: ReserverService

    override fun getAllReservation(sectionId: UUID, pageable: Pageable, predicate: Predicate): Page<ReservationDto> {
        val newPredicate = ExpressionUtils.allOf(predicate, QSection.section.id.eq(sectionId))!!

        reservationRepository.findAll(newPredicate, pageable).run {
            return this.map {
                it.toReservationDto() }
        }
    }

    @Transactional
    override fun createReservation(sectionId: UUID, reservation: ReservationCreateDto): ReservationDto {
        // Check for overlapping reservations at same section
        if (reservationRepository.existsInterval(reservation.fromTime!!, reservation.toTime!!, sectionId)) {
            throw ApplicationException.throwException(
                    EntityType.RESERVATION, ExceptionType.DUPLICATE_ENTITY, reservation.fromTime.toString(), reservation.toTime.toString())
        }
        val section = sectionRepository.findById(sectionId).orElseThrow { throw ApplicationException.throwException(
            EntityType.SECTION, ExceptionType.ENTITY_NOT_FOUND, reservation.sectionId.toString()) }
        // Check for overlapping reservations at parent sections
        if (section.parent != null && reservationRepository.existsInterval(reservation.fromTime!!, reservation.toTime!!, section.parent!!.id)) {
            throw ApplicationException.throwException(
                EntityType.RESERVATION, ExceptionType.DUPLICATE_ENTITY, reservation.fromTime.toString(), reservation.toTime.toString())
        }
        // Check for overlapping reservations at child sections
        section.children.forEach { child -> if (reservationRepository.existsInterval(reservation.fromTime!!, reservation.toTime!!, child.id)) {
                throw ApplicationException.throwException(
                    EntityType.RESERVATION, ExceptionType.DUPLICATE_ENTITY, reservation.fromTime.toString(), reservation.toTime.toString())
            }
        }

        var newReservation = reservation.toReservation()

        reserverService = reserverServiceResolver.resolveService(reservation.type)
        val entity = reserverService.getReserverById(reservation.entityId!!)

        newReservation.setRelation(entity)
        newReservation.section = section
        newReservation = reservationRepository.save(newReservation)
        return newReservation.toReservationDto()
    }

    override fun getUserReservation(userId: UUID, pageable: Pageable, predicate: Predicate): Page<ReservationDto> {
        val reservation = QReservation.reservation
        val userReservation = reservation.`as`(QUserReservation::class.java)
        val newPredicate = ExpressionUtils.allOf(predicate, userReservation.user.id.eq(userId))!!
        reservationRepository.findAll(newPredicate, pageable).run {
            return this.map { it.toReservationDto() }
        }
    }

    override fun getReservation(sectionId: UUID, reservationId: UUID): ReservationDto =
        reservationRepository.findById(reservationId).orElseThrow { throw ApplicationException.throwException(
                EntityType.RESERVATION, ExceptionType.ENTITY_NOT_FOUND, reservationId.toString(), sectionId.toString()) }
                .run {
                    return this.toReservationDto()
            }

    @Transactional
    override fun updateReservation(sectionId: UUID, reservationId: UUID, reservation: ReservationDto): ReservationDto {
        reservationRepository.findReservationByIdAndSectionId(reservationId, sectionId).run {
            if (this != null) {
                this.text = reservation.text
                this.nrOfPeople = reservation.nrOfPeople
                reservationRepository.save(this).run {
                    return this.toReservationDto()
                }
            }
        }
        throw ApplicationException.throwException(
                EntityType.RESERVATION, ExceptionType.ENTITY_NOT_FOUND, reservationId.toString(), sectionId.toString())
    }

    @Transactional
    override fun deleteReservation(sectionId: UUID, reservationId: UUID) {
        reservationRepository.findById(reservationId).orElseThrow { throw ApplicationException.throwException(
                EntityType.RESERVATION, ExceptionType.ENTITY_NOT_FOUND, reservationId.toString(), sectionId.toString()) }
                .run {
            reservationRepository.delete(this)
        }
    }

    override fun getGroupReservation(groupId: UUID, pageable: Pageable, predicate: Predicate): Page<ReservationDto> {
        val reservation = QReservation.reservation
        val groupReservation = reservation.`as`(QGroupReservation::class.java)
        val newPredicate = ExpressionUtils.allOf(predicate, groupReservation.group.id.eq(groupId))!!
        reservationRepository.findAll(newPredicate, pageable).run {
            return this.map { it.toReservationDto() }
        }
    }
}
