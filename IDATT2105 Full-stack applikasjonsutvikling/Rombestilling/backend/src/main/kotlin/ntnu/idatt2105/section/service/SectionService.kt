package ntnu.idatt2105.section.service

import com.querydsl.core.types.Predicate
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.section.dto.CreateSectionRequest
import ntnu.idatt2105.section.dto.SectionDto
import ntnu.idatt2105.section.dto.SectionListDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface SectionService {
    fun getAllSections(pageable: Pageable, predicate: Predicate): Page<SectionListDto>
    fun createSection(createSectionRequest: CreateSectionRequest): SectionDto
    fun getSectionById(id: UUID): SectionDto
    fun updateSection(id: UUID, section: SectionDto): SectionDto
    fun deleteSection(id: UUID): Response
}
