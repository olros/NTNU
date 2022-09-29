package ntnu.idatt2105.section.service

import com.querydsl.core.types.Predicate
import ntnu.idatt2105.core.exception.ApplicationException
import ntnu.idatt2105.core.exception.EntityType
import ntnu.idatt2105.core.exception.ExceptionType
import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.section.dto.*
import ntnu.idatt2105.section.dto.CreateSectionRequest
import ntnu.idatt2105.section.dto.SectionDto
import ntnu.idatt2105.section.dto.SectionListDto
import ntnu.idatt2105.section.model.Section
import ntnu.idatt2105.section.repository.SectionRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class SectionServiceImpl(
    val sectionRepository: SectionRepository,
    val sectionFactory: SectionFactoryImpl
) : SectionService {

    val logger = LoggerFactory.getLogger("SectionService")

    override fun getAllSections(pageable: Pageable, predicate: Predicate): Page<SectionListDto> {
        val sections = sectionRepository.findAll(predicate, pageable)
        return sections.map { it.toSectionListDto() }
    }

    @Transactional
    override fun createSection(createSectionRequest: CreateSectionRequest): SectionDto {
        if (createSectionRequest.parentId != null)
            return getSection(createSectionRequest.parentId!!)
                .run { sectionFactory.createChildSection(createSectionRequest, parent = this) }
                .toSectionDto()

        return sectionFactory.createParentSection(createSectionRequest).toSectionDto()
    }

    private fun getSection(id: UUID): Section =
        sectionRepository.findById(id).orElseThrow { throw ApplicationException.throwException(
            EntityType.SECTION, ExceptionType.ENTITY_NOT_FOUND, id.toString()) }

    override fun getSectionById(id: UUID): SectionDto {
        val section = getSection(id)
        logger.info("Finding section by id: $id")
        return section.toSectionDto()
    }

    @Transactional
    override fun updateSection(id: UUID, section: SectionDto): SectionDto {
        getSection(id).run {
            var updatedSection = this.copy(
                    name = section.name,
                    capacity = section.capacity,
                image = section.image,
                    description = section.description
            )
            updatedSection = sectionRepository.save(updatedSection)
            logger.info("Section: ${section.name} was updated")
            return updatedSection.toSectionDto()
        }
    }

    @Transactional
    override fun deleteSection(id: UUID): Response {
        getSection(id).run {
            sectionRepository.delete(this)
            logger.info("Section with id: $id was deleted")
            return Response("Section was deleted")
        }
    }
}
