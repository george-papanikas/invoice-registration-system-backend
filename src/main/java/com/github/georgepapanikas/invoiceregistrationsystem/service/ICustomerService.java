package com.github.georgepapanikas.invoiceregistrationsystem.service;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.CustomerInsertDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.CustomerUpdateDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Customer;
import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;

/**
 * Service interface defining operations for managing {@link Customer} entities.
 *
 * <p>Implementations should handle creation, update, deletion, and retrieval
 * of customers, including validation, mapping from DTOs, and exception handling.</p>
 */
public interface ICustomerService {

    /**
     * Inserts a new customer into the system.
     *
     * <p>Maps the provided {@link CustomerInsertDTO} to a {@link Customer} entity,
     * persists it, and returns the saved entity.</p>
     *
     * @param dto the DTO containing customer details to insert
     * @return the newly created {@link Customer} entity
     * @throws Exception if the insertion fails (e.g., constraint violations)
     */
    Customer insertCustomer(CustomerInsertDTO dto) throws Exception;

    /**
     * Updates an existing customer’s information.
     *
     * <p>Locates the customer by ID, applies the updates from
     * {@link CustomerUpdateDTO}, persists the changes, and returns the updated entity.</p>
     *
     * @param id  the ID of the customer to update
     * @param dto the DTO containing updated customer fields
     * @return the updated {@link Customer} entity
     * @throws EntityNotFoundException if no customer exists with the given ID
     */
    Customer updateCustomer(Long id, CustomerUpdateDTO dto) throws EntityNotFoundException;

    /**
     * Deletes a customer by ID.
     *
     * <p>Removes the specified customer from the database and returns the deleted entity
     * for confirmation or logging.</p>
     *
     * @param id the ID of the customer to delete
     * @return the deleted {@link Customer} entity
     * @throws EntityNotFoundException        if no customer exists with the given ID
     * @throws DataIntegrityViolationException if the customer cannot be deleted due to
     *                                         existing references (e.g., invoices)
     */
    Customer deleteCustomer(Long id) throws DataIntegrityViolationException, EntityNotFoundException;

    /**
     * Retrieves all customers in the system.
     *
     * @return a {@link List} of all {@link Customer} entities
     * @throws EntityNotFoundException if no customers are found
     */
    List<Customer> getAllCustomers() throws EntityNotFoundException;

    /**
     * Retrieves a customer by their ID.
     *
     * @param id the ID of the customer to retrieve
     * @return the matching {@link Customer} entity
     * @throws EntityNotFoundException if no customer exists with the given ID
     */
    Customer getCustomerById(Long id) throws EntityNotFoundException;
}
