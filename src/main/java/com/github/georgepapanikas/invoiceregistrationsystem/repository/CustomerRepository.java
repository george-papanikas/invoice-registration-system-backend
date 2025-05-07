package com.github.georgepapanikas.invoiceregistrationsystem.repository;

import com.github.georgepapanikas.invoiceregistrationsystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link Customer} entities.
 * Extends Spring Data’s {@link JpaRepository} to inherit standard data access methods.
 *
 * <p>Additional query methods can be defined here and will be automatically implemented
 * by Spring Data JPA based on their names.</p>
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Retrieves a {@link Customer} by its VAT number.
     *
     * @param number the VAT number to search for (must be a nine‑digit string)
     * @return an {@link Optional} containing the matching Customer if found, or empty if none exists
     */
    Optional<Customer> findByVatNumber(String number);
}
