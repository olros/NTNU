package ntnu.idatt2105.section.controller

import com.querydsl.core.types.Predicate
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.section.dto.CreateSectionRequest
import ntnu.idatt2105.section.dto.SectionDto
import ntnu.idatt2105.section.service.SectionService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class SectionControllerImpl(val sectionService: SectionService) : SectionController {

    override fun getAllSections(predicate: Predicate, pageable: Pageable) =
        sectionService.getAllSections(pageable, predicate)

    override fun createSection(CreateSectionRequest: CreateSectionRequest) =
            ResponseEntity(sectionService.createSection(CreateSectionRequest), HttpStatus.CREATED)

    override fun getSection(sectionId: UUID) =
        ResponseEntity(sectionService.getSectionById(sectionId), HttpStatus.OK)

    override fun updateSection(sectionId: UUID, section: SectionDto) =
            ResponseEntity(sectionService.updateSection(sectionId, section), HttpStatus.OK)

    override fun deleteSection(sectionId: UUID): ResponseEntity<Response> {
        sectionService.deleteSection(sectionId)
        return ResponseEntity(Response("Section has been deleted"), HttpStatus.OK)
    }
}
