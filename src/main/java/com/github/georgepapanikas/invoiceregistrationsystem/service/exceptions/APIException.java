package com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown to signal API errors with an associated HTTP status code.
 * <p>
 * Use this exception in service or controller layers to propagate an error
 * condition along with the desired HTTP response status.
 * </p>
 */
@Getter
@AllArgsConstructor
public class APIException extends RuntimeException {

    /**
     * The HTTP status that should be returned to the client when this exception is handled.
     */
    private HttpStatus status;

    /**
     * A human‑readable message describing the error condition.
     */
    private String message;
}
