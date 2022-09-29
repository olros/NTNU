package ntnu.idatt2105.security.exception.handler

import ntnu.idatt2105.core.response.Response
import ntnu.idatt2105.security.exception.InvalidJwtToken
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class InvalidJwtTokenHandler {
    @ExceptionHandler(InvalidJwtToken::class)
    protected fun invalidJwtToken(ex: InvalidJwtToken, request: WebRequest): ResponseEntity<Any> =
            ResponseEntity(Response(ex.message!!), HttpStatus.UNAUTHORIZED)
}
