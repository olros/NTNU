package ntnu.idatt2105.core.exception.handler

import ntnu.idatt2105.core.response.ResponseError
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@Order(1)
@RestControllerAdvice
class MethodArgumentInvalidHandler {

    /**
     * Return response validation error with the the errors mapped
     * and appropriate error message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(exception: MethodArgumentNotValidException): ResponseError? {
        val fieldErrorMessages = getFieldErrors(exception)
        val globalErrorMessages: MutableMap<String, Any?> = getGlobalErrors(exception)
        val message = getMessage(exception)

        includeFieldErrors(fieldErrorMessages, globalErrorMessages)

        return ResponseError.validationError(message, globalErrorMessages)
    }

    /**
     * Return map of field to field error messages
     */
    private fun getFieldErrors(exception: MethodArgumentNotValidException): Map<String, String?> =
        exception.bindingResult
            .fieldErrors
            .map { it.field to it.defaultMessage }
            .toMap()

    private fun getGlobalErrors(exception: MethodArgumentNotValidException): MutableMap<String, Any?> =
        exception.globalErrors
            .map { it.code.toString() to it.defaultMessage }
            .toMap()
            .toMutableMap()

    /**
     * Return appropriate message from the exception.
     *
     * If it has global errors use the first message, if not use the first field message if present.
     * Defaults to a generic message.
     */
    private fun getMessage(
        exception: MethodArgumentNotValidException,
    ) = when {
            exception.globalErrors.isNotEmpty() -> exception.globalErrors[0].defaultMessage.toString()
            exception.fieldErrors.isNotEmpty() -> exception.fieldErrors[0].defaultMessage.toString()
            else -> "One or more arguments are invalid"
        }

    /**
     * Add field errors map to the global map if present.
     */
    private fun includeFieldErrors(
        fieldErrorMessages: Map<String, String?>,
        globalErrorMessages: MutableMap<String, Any?>
    ) {
        if (fieldErrorMessages.isNotEmpty())
            globalErrorMessages["fields"] = fieldErrorMessages
    }
}
