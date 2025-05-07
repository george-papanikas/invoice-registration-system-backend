package com.github.georgepapanikas.invoiceregistrationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Invoice Registration System application.
 * <p>
 * Annotated with {@link SpringBootApplication} to enable component scanning,
 * auto‑configuration, and property support. Bootstraps the Spring context
 * and starts the embedded server.
 * </p>
 */
@SpringBootApplication
public class InvoiceRegistrationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceRegistrationSystemApplication.class, args);
	}
}
