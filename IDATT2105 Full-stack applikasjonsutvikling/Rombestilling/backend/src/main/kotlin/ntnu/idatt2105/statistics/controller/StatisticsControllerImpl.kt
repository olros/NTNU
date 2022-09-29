package ntnu.idatt2105.statistics.controller

import com.querydsl.core.types.Predicate
import ntnu.idatt2105.statistics.dto.StatisticsDto
import ntnu.idatt2105.statistics.service.StatisticsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class StatisticsControllerImpl(val statisticsService: StatisticsService) : StatisticsController {

    override fun getStatistics(sectionId: UUID, predicate: Predicate): ResponseEntity<StatisticsDto> =
        ResponseEntity(statisticsService.getStatisticsForSection(sectionId, predicate), HttpStatus.OK)
}
