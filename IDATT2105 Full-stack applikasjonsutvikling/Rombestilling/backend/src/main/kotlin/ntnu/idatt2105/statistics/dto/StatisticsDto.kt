package ntnu.idatt2105.statistics.dto

data class StatisticsDto(
    val nrOfReservation: Int,
    val hoursOfReservation: Long,
    val daysWithReservation: Int,
    val userReservationCount: Int,
)
