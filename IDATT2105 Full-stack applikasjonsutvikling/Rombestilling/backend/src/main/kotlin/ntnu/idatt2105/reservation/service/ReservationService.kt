package ntnu.idatt2105.reservation.service

import com.querydsl.core.types.Predicate
import ntnu.idatt2105.reservation.dto.ReservationCreateDto
import ntnu.idatt2105.reservation.dto.ReservationDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface ReservationService {
    fun getAllReservation(sectionId: UUID, pageable: Pageable, predicate: Predicate): Page<ReservationDto>
    fun createReservation(sectionId: UUID, reservation: ReservationCreateDto): ReservationDto
    fun getUserReservation(userId: UUID, pageable: Pageable, predicate: Predicate): Page<ReservationDto>
    fun getGroupReservation(groupId: UUID, pageable: Pageable, predicate: Predicate): Page<ReservationDto>
    fun updateReservation(sectionId: UUID, reservationId: UUID, reservation: ReservationDto): ReservationDto
    fun deleteReservation(sectionId: UUID, reservationId: UUID)
    fun getReservation(sectionId: UUID, reservationId: UUID): ReservationDto
}
