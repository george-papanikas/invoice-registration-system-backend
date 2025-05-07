package com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Data transfer object containing structured information about an error
 * that occurred during API processing. Sent back to clients when exceptions
 * are thrown, providing a consistent error payload.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {

    /**
     * The timestamp when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * The HTTP status code associated with the error (e.g., 400).
     */
    private int status;

    /**
     * The HTTP status reason phrase (e.g., "Bad Request").
     */
    private String error;

    /**
     * The request path (URI) where the error was triggered.
     */
    private String path;

    /**
     * A message describing the error in more detail.
     */
    private String message;
}
