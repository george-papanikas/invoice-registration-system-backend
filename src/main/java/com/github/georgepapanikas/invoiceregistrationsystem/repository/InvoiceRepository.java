package com.github.georgepapanikas.invoiceregistrationsystem.repository;

import com.github.georgepapanikas.invoiceregistrationsystem.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link Invoice} entities.
 * Extends Spring Data’s {@link JpaRepository} to inherit standard data access methods.
 *
 * <p>Additional query methods can be declared here and will be auto‑implemented
 * by Spring Data JPA based on method naming conventions.</p>
 */
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Retrieves an {@link Invoice} by its unique invoice number.
     *
     * @param number the invoice number to search for (e.g. “INV-0001”)
     * @return an {@link Optional} containing the matching Invoice if found, or empty if none exists
     */
    Optional<Invoice> findByNumber(String number);
    /**
     * Count how many invoices belong to the given customer.
     *
     * @param customerId the ID of the customer
     * @return the number of invoices for that customer
     */
    Long countByCustomerId(Long customerId);
}
