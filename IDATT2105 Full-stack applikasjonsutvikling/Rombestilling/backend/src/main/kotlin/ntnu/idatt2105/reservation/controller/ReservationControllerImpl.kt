package ntnu.idatt2105.reservation.controller

import com.querydsl.core.types.Predicate
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.reservation.dto.ReservationCreateDto
import ntnu.idatt2105.reservation.dto.ReservationDto
import ntnu.idatt2105.reservation.service.ReservationService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class ReservationControllerImpl(val reservationService: ReservationService) : ReservationController {

    override fun getAllReservations(
        predicate: Predicate,
        pageable: Pageable,
        sectionId: UUID
    ) = reservationService.getAllReservation(sectionId, pageable, predicate)

    override fun getReservation(sectionId: UUID, reservationId: UUID) =
        reservationService.getReservation(sectionId, reservationId)

    override fun createReservation(sectionId: UUID, reservation: ReservationCreateDto) =
        reservationService.createReservation(sectionId, reservation)

    override fun updateReservation(sectionId: UUID, reservationId: UUID, reservation: ReservationDto) =
        reservationService.updateReservation(sectionId, reservationId, reservation)

    override fun deleteReservation(sectionId: UUID, reservationId: UUID): ResponseEntity<Response> {
        reservationService.deleteReservation(sectionId, reservationId)
        return ResponseEntity.ok(Response("Reservation deleted"))
    }
}
