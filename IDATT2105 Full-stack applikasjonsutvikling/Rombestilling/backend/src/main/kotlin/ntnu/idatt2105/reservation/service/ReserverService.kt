package ntnu.idatt2105.reservation.service

import ntnu.idatt2105.reservation.model.Reserver
import java.util.*

interface ReserverService {
    fun getReserverById(id: UUID): Reserver
}
