package ntnu.idatt2105.core.exception

import ntnu.idatt2105.core.config.PropertiesConfig
import org.springframework.stereotype.Component
import java.text.MessageFormat
import java.util.*

/**
 * Helper class to generate domain specific RuntimeExceptions.
 */
@Component
class ApplicationException(propertiesConfig: PropertiesConfig) {

    class EntityNotFoundException(message: String?) : RuntimeException(message)
    class DuplicateEntityException(message: String?) : RuntimeException(message)
    class EntityNotValidException(message: String?) : RuntimeException(message)

    companion object {
        private lateinit var propertiesConfig: PropertiesConfig

        /**
         * Returns new RuntimeException based on EntityType, ExceptionType and args
         */
        fun throwException(
            entityType: EntityType,
            exceptionType: ExceptionType,
            vararg args: String
        ): RuntimeException {
            val messageTemplate = getMessageTemplate(entityType, exceptionType)
            return throwException(exceptionType, messageTemplate, *args)
        }

        /**
         * Returns new RuntimeException based on template and args
         */
        private fun throwException(
            exceptionType: ExceptionType?,
            messageTemplate: String,
            vararg args: String
        ): RuntimeException {
            if (ExceptionType.ENTITY_NOT_FOUND == exceptionType) {
                return EntityNotFoundException(format(messageTemplate, *args))
            } else if (ExceptionType.DUPLICATE_ENTITY == exceptionType) {
                return DuplicateEntityException(format(messageTemplate, *args))
            } else if (ExceptionType.NOT_VALID == exceptionType) {
                return EntityNotValidException(format(messageTemplate, *args))
            }
            return RuntimeException(format(messageTemplate, *args))
        }

        /**
         * Returns new RuntimeException based on EntityType, ExceptionType and args
         */
        fun throwExceptionWithId(
            entityType: EntityType,
            exceptionType: ExceptionType,
            id: String,
            vararg args: String
        ): RuntimeException {
            val messageTemplate = getMessageTemplate(entityType, exceptionType).plus(".").plus(id)
            return throwException(exceptionType, messageTemplate, *args)
        }

        private fun getMessageTemplate(entityType: EntityType, exceptionType: ExceptionType): String {
            return entityType.name.plus(".").plus(exceptionType.value).toLowerCase()
        }

        private fun format(template: String, vararg args: String): String {
            val templateContent: Optional<String> = Optional.ofNullable(propertiesConfig.getConfigValue(template))
            return templateContent.map { s -> MessageFormat.format(s, *args) }
                .orElseGet { String.format(template, *args) }
        }
    }

    init {
        Companion.propertiesConfig = propertiesConfig
    }
}
