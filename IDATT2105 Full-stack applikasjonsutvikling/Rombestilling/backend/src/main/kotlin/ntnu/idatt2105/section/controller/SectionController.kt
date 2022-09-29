package ntnu.idatt2105.section.controller

import com.querydsl.core.types.Predicate
import io.swagger.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.core.util.PaginationConstants
import ntnu.idatt2105.section.dto.CreateSectionRequest
import ntnu.idatt2105.section.dto.SectionDto
import ntnu.idatt2105.section.dto.SectionListDto
import ntnu.idatt2105.section.model.Section
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.querydsl.binding.QuerydslPredicate
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@Api(value = "Section services", tags = ["Section Services"], description = "Section Services")
@RequestMapping("/sections/")
interface SectionController {

    @Operation(summary = "Fetch sections", responses = [ApiResponse(responseCode = "200", description = "Success")])
    @GetMapping
    fun getAllSections(
        @QuerydslPredicate(root = Section::class) predicate: Predicate,
        @PageableDefault(size = PaginationConstants.PAGINATION_SIZE,
         sort = ["name"], direction = Sort.Direction.DESC) pageable: Pageable
    ): Page<SectionListDto>

    @Operation(summary = "Create a new section", responses = [
        ApiResponse(responseCode = "201", description = "Created: new section was created"),
        ApiResponse(responseCode = "400", description = "Bad request: new section was not created"),
    ])
    @PostMapping
    fun createSection(@RequestBody CreateSectionRequest: @Valid CreateSectionRequest): ResponseEntity<SectionDto>

    @Operation(summary = "Fetch section details for the given section id", responses = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "404", description = "Not found: section with the given id does not exist")
    ])
    @GetMapping("{sectionId}/")
    fun getSection(@PathVariable sectionId: UUID): ResponseEntity<SectionDto>

    @Operation(summary = "Update existing section", responses = [
        ApiResponse(responseCode = "200", description = "Success: section was updated"),
        ApiResponse(responseCode = "400", description = "Bad request: existing section was not updated"),
        ApiResponse(responseCode = "404", description = "Not found: section with the given id does not exist"),
    ])
    @PutMapping("{sectionId}/")
    fun updateSection(@PathVariable sectionId: UUID, @RequestBody section: SectionDto): ResponseEntity<SectionDto>

    @Operation(summary = "Delete existing section", responses = [
        ApiResponse(responseCode = "200", description = "Success: section was deleted"),
        ApiResponse(responseCode = "404", description = "Not found: section with the given id does not exist"),
    ])
    @DeleteMapping("{sectionId}/")
    fun deleteSection(@PathVariable sectionId: UUID): ResponseEntity<Response>
}
