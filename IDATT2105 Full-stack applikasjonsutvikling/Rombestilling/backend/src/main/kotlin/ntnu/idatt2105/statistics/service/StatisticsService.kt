package ntnu.idatt2105.statistics.service

import com.querydsl.core.types.Predicate
import ntnu.idatt2105.statistics.dto.StatisticsDto
import java.util.*

interface StatisticsService {
    fun getStatisticsForSection(sectionID: UUID, predicate: Predicate): StatisticsDto
}
