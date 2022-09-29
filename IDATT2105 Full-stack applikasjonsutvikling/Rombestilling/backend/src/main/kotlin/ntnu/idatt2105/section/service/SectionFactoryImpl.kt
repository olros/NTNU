package ntnu.idatt2105.section.service

import ntnu.idatt2105.core.exception.ApplicationException
import ntnu.idatt2105.core.exception.EntityType
import ntnu.idatt2105.core.exception.ExceptionType
import ntnu.idatt2105.section.dto.CreateSectionRequest
import ntnu.idatt2105.section.dto.toSection
import ntnu.idatt2105.section.model.Section
import ntnu.idatt2105.section.repository.SectionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SectionFactoryImpl(val sectionRepository: SectionRepository) : SectionFactory {
    val logger = LoggerFactory.getLogger(this::class.java.name)

    override fun createParentSection(createSection: CreateSectionRequest): Section {
        logger.info("Creating new parent section (id:${createSection.id})")

        val section = createSection.toSection()

        return saveSection(section)
    }

    private fun saveSection(section: Section): Section {
        section.children = mutableListOf()
        val sectionCreated = sectionRepository.save(section)

        logger.info("Successfully created section (id:${sectionCreated.id})")

        return sectionCreated
    }

    override fun createChildSection(createSection: CreateSectionRequest, parent: Section): Section {
        logger.info("Creating new child section (id:${createSection.id})")

        val section = createSection.toSection()
        section.parent = parent
        validateCapacity(parent, section)
        validateParentType(parent, section)

        return saveSection(section)
    }

    private fun validateParentType(parent: Section, section: Section) {
        if (!parent.isRoom())
            throw ApplicationException.throwExceptionWithId(
                EntityType.SECTION,
                ExceptionType.NOT_VALID,
                "invalidParentType"
            )
    }

    private fun validateCapacity(
        parent: Section,
        section: Section
    ) {
        if (parent.hasNoCapacityFor(section))
            throw ApplicationException.throwExceptionWithId(
                EntityType.SECTION, ExceptionType.NOT_VALID, "capacity"
            )
    }
}
