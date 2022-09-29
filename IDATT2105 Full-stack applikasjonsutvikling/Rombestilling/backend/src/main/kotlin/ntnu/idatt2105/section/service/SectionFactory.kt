package ntnu.idatt2105.section.service

import ntnu.idatt2105.section.dto.CreateSectionRequest
import ntnu.idatt2105.section.model.Section

interface SectionFactory {
    fun createParentSection(createSection: CreateSectionRequest): Section
    fun createChildSection(createSection: CreateSectionRequest, parent: Section): Section
}
