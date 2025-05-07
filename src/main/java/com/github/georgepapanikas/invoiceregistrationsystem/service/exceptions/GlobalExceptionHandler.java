package com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;

/**
 * Global exception handler that intercepts exceptions thrown by controllers
 * and services across the application and translates them into
 * structured HTTP error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom {@link APIException} instances.
     * <p>
     * When an APIException is thrown, this method builds an {@link ErrorDetails}
     * object containing:
     * <ul>
     *   <li>Timestamp of the error</li>
     *   <li>HTTP status code and reason phrase</li>
     *   <li>Request description (URI)</li>
     *   <li>Exception message</li>
     * </ul>
     * and returns it with a 400 Bad Request status.
     *
     * @param exception the caught APIException with status and message
     * @param request   the current web request context
     * @return a {@link ResponseEntity} containing the populated ErrorDetails
     *         and {@link HttpStatus#BAD_REQUEST}
     */
    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorDetails> handleAPIException(APIException exception, WebRequest request){
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                exception.getStatus().value(),
                exception.getStatus().getReasonPhrase(),
                request.getDescription(false),
                exception.getMessage()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles authentication failures caused by bad credentials.
     *
     * @return a 401 Unauthorized response with a generic error message
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
    }
}
