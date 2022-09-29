package ntnu.idatt2105.reservation.service

interface ReserverServiceResolver {
    fun resolveService(reservationType: String?): ReserverService
}
