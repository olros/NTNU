package com.ntnu.gidd.exception;

import com.ntnu.gidd.util.Response;
import com.ntnu.gidd.util.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

      @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
      @ExceptionHandler(Exception.class)
      public Response handleUncaughtException(MessagingException exception){
            log.error("[X] Error caught while processing request {}", exception.getMessage());
            return new Response(exception.getMessage());
      }

      @ResponseStatus(HttpStatus.BAD_REQUEST)
      @ExceptionHandler(MethodArgumentNotValidException.class)
      public ExceptionResponse handleValidationExceptions(MethodArgumentNotValidException exception){
            Map<String, String> errorMessages = new HashMap<>();

            exception.getBindingResult().getFieldErrors().forEach(error -> {
                  errorMessages.put(error.getField(), error.getDefaultMessage());
            });

            String message = "One or more method arguments are invalid";

            log.error("[X] Error caught {}", message);
            return new ExceptionResponse(message, errorMessages);
      }

      @ResponseStatus(value = HttpStatus.NOT_FOUND)
      @ExceptionHandler({EntityNotFoundException.class})
      public Response handleEntityNotFound(EntityNotFoundException exception){
            log.error("[X] Error caught while processing request {}", exception.getMessage());
            return new Response(exception.getMessage());
      }

      @ResponseStatus(value = HttpStatus.FORBIDDEN)
      @ExceptionHandler(NotInvitedException.class)
      public Response handleNotInvited(NotInvitedException exception){
            log.error("[X] Error caught while processing request {}", exception.getMessage());
            return new Response(exception.getMessage());
      }

      @ResponseStatus(value = HttpStatus.FORBIDDEN)
      @ExceptionHandler(InvalidUnInviteException.class)
      public Response handleNotAbleToUnInvite(InvalidUnInviteException exception){
            log.error("[X] Error caught while processing request {}", exception.getMessage());
            return new Response(exception.getMessage());
      }

      @ResponseStatus(value = HttpStatus.FORBIDDEN)
      @ExceptionHandler(EmailInUseException.class)
      public Response handleEmailInUse(EmailInUseException exception){
            log.error("[X] Error caught while processing request {}", exception.getMessage());
            return new Response(exception.getMessage());
      }

      @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
      @ExceptionHandler(PasswordIsIncorrectException.class)
      public Response handlePasswordIncorrect(PasswordIsIncorrectException exception){
            log.error("[X] Error caught while processing request {}", exception.getMessage());
            return new Response(exception.getMessage());
      }

      @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
      @ExceptionHandler(MessagingException.class)
      public Response handleMessageException(MessagingException exception){
            log.error("[X] Error caught while processing request {}", exception.getMessage());
            return new Response(exception.getMessage());
      }

      @ResponseStatus(value = HttpStatus.BAD_REQUEST)
      @ExceptionHandler({InvalidJwtToken.class})
      public Response handleInvalidToken(ResetPasswordTokenNotFoundException exception){
            log.error("[X] Error caught while processing request {}", exception.getMessage());
            return new Response(exception.getMessage());
      }

      @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
      @ExceptionHandler({RefreshTokenNotFound.class})
      public Response handleRefreshTokenNotFound(RefreshTokenNotFound exception){
            log.error("[X] Error caught while processing request {}", exception.getMessage());
            return new Response(exception.getMessage());
      }

      @ResponseStatus(value = HttpStatus.BAD_REQUEST)
      @ExceptionHandler({InvalidFollowRequestException.class})
      public Response handleInvalidFollowRequest(InvalidFollowRequestException exception){
            log.error("[X] Error caught while processing request {}", exception.getMessage());
            return new Response(exception.getMessage());
      }
}
