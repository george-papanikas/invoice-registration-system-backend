package com.github.georgepapanikas.invoiceregistrationsystem.service;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceInsertDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceUpdateDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Invoice;
import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.EntityNotFoundException;
import java.util.List;

/**
 * Service interface defining operations for managing {@link Invoice} entities.
 *
 * <p>Implementations should handle creation, update, deletion, and retrieval
 * of invoices, including mapping from DTOs, validation, and exception handling.</p>
 */
public interface IInvoiceService {

    /**
     * Inserts a new invoice into the system.
     *
     * <p>Maps the provided {@link InvoiceInsertDTO} to an {@link Invoice} entity,
     * persists it, and returns the saved entity.</p>
     *
     * @param dto the DTO containing invoice details to insert
     * @return the newly created {@link Invoice} entity
     * @throws Exception if the insertion fails (e.g., constraint violations or persistence errors)
     */
    Invoice insertInvoice(InvoiceInsertDTO dto) throws Exception;

    /**
     * Updates an existing invoice’s data.
     *
     * <p>Locates the invoice by its ID, applies updates from the provided
     * {@link InvoiceUpdateDTO}, persists the changes, and returns the updated entity.</p>
     *
     * @param id  the ID of the invoice to update
     * @param dto the DTO containing updated invoice fields
     * @return the updated {@link Invoice} entity
     * @throws EntityNotFoundException if no invoice exists with the given ID
     */
    Invoice updateInvoice(Long id, InvoiceUpdateDTO dto) throws EntityNotFoundException;

    /**
     * Deletes an invoice by its ID.
     *
     * <p>Removes the specified invoice from the database and returns the deleted
     * entity for confirmation or logging.</p>
     *
     * @param id the ID of the invoice to delete
     * @return the deleted {@link Invoice} entity
     * @throws EntityNotFoundException if no invoice exists with the given ID
     */
    Invoice deleteInvoice(Long id) throws EntityNotFoundException;

    /**
     * Retrieves all invoices in the system.
     *
     * @return a {@link List} of all {@link Invoice} entities
     * @throws EntityNotFoundException if no invoices are found
     */
    List<Invoice> getAllInvoices() throws EntityNotFoundException;

    /**
     * Retrieves a single invoice by its ID.
     *
     * @param id the ID of the invoice to retrieve
     * @return the matching {@link Invoice} entity
     * @throws EntityNotFoundException if no invoice exists with the given ID
     */
    Invoice getInvoiceById(Long id) throws EntityNotFoundException;
}

