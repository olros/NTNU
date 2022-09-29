package ntnu.idatt2105.core.response

import java.time.LocalDate

data class ResponseError(
    val message: String?,
    val errors: Any?,
    val status: Status,
    val timestamp: LocalDate = LocalDate.now()
) {

    companion object {
        fun notFound(message: String?, exception: Exception) =
            ResponseError(
                message,
                exception.message,
                Status.NOT_FOUND,
            )

        fun duplicateEntity(message: String?, exception: Exception): ResponseError =
            ResponseError(
                message,
                exception.message,
                Status.DUPLICATE_ENTITY,
            )

        fun validationError(message: String?, errors: Any?): ResponseError =
            ResponseError(
                message,
                errors,
                Status.VALIDATION_EXCEPTION
            )
    }

    enum class Status {
        OK,
        BAD_REQUEST,
        UNAUTHORIZED,
        VALIDATION_EXCEPTION,
        EXCEPTION,
        WRONG_CREDENTIALS,
        ACCESS_DENIED,
        NOT_FOUND,
        DUPLICATE_ENTITY
    }
}
