package ntnu.idatt2105.statistics.controller

import com.querydsl.core.types.Predicate
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import ntnu.idatt2105.reservation.model.Reservation
import ntnu.idatt2105.statistics.dto.StatisticsDto
import org.springframework.data.querydsl.binding.QuerydslPredicate
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

@Api(value = "Statistics for Section", tags = ["Statistics for section"], description = "Statistics for section")
@RequestMapping("/sections/{sectionId}/statistics/")
interface StatisticsController {

    @Operation(
        summary = "Get statistics from a section", responses = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad request: could not get statistics"),
        ]
    )
    @GetMapping
    fun getStatistics(
        @PathVariable sectionId: UUID,
        @QuerydslPredicate(root = Reservation::class) predicate: Predicate
    ): ResponseEntity<StatisticsDto>
}
