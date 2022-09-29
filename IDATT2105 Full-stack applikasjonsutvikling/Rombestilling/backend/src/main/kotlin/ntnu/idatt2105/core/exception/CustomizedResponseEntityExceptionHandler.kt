package ntnu.idatt2105.core.exception

import ntnu.idatt2105.core.response.ResponseError
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.Exception

@Order(2)
@RestControllerAdvice
class CustomizedResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ApplicationException.EntityNotFoundException::class)
    fun handleNotFountExceptions(ex: Exception, request: WebRequest?): ResponseEntity<*> {

        val responseError: ResponseError = ResponseError.notFound(ex.message, ex)
        return ResponseEntity<Any?>(responseError, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ApplicationException.DuplicateEntityException::class)
    fun handleDuplicateEntityExceptions(ex: Exception, request: WebRequest?): ResponseEntity<*> {

        val responseError: ResponseError = ResponseError.duplicateEntity(ex.message, ex)
        return ResponseEntity<Any?>(responseError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ApplicationException.EntityNotValidException::class)
    fun handleEntityNotValidException(ex: Exception, request: WebRequest?): ResponseEntity<*> {

        val responseError: ResponseError = ResponseError.validationError(ex.message, ex)
        return ResponseEntity<Any?>(responseError, HttpStatus.BAD_REQUEST)
    }
}
