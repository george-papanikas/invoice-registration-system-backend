package com.github.georgepapanikas.invoiceregistrationsystem.service;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceInsertDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceUpdateDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.mapper.InvoiceMapper;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Customer;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Invoice;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.CustomerRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.InvoiceRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.EntityAlreadyExistsException;
import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * {@link IInvoiceService} implementation handling invoice lifecycle operations.
 *
 * <p>Uses {@link InvoiceRepository} for CRUD operations on {@link Invoice}
 * and {@link CustomerRepository} to resolve customer associations.
 * Applies transaction boundaries and logs each operation's outcome.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements IInvoiceService {


    /**
     * Repository for invoice persistence operations.
     */
    private final InvoiceRepository invoiceRepository;

    /**
     * Repository for customer lookup when associating invoices.
     */
    private final CustomerRepository customerRepository;

    /**
     * Inserts a new invoice into the system.
     *
     * <p>Steps performed:
     * <ol>
     *   <li>Checks if an invoice with the same number already exists,
     *       throwing {@link EntityAlreadyExistsException} if so.</li>
     *   <li>Fetches the {@link Customer} by ID, or throws {@link EntityNotFoundException}.</li>
     *   <li>Maps the {@link InvoiceInsertDTO} to an {@link Invoice} entity.</li>
     *   <li>Associates the fetched customer and saves the invoice.</li>
     *   <li>Verifies that an ID was generated, or throws a generic {@link Exception}.</li>
     * </ol>
     *
     * @param dto the DTO containing invoice details to insert
     * @return the newly created {@link Invoice} entity with generated ID
     * @throws EntityAlreadyExistsException if an invoice with the same number exists
     * @throws EntityNotFoundException      if the specified customer ID does not exist
     * @throws Exception                    if the save operation does not generate an ID
     */
    @Transactional
    @Override
    public Invoice insertInvoice(InvoiceInsertDTO dto) throws Exception{
        Invoice invoice = null;
        Customer customer = null;

        try {
            Optional<Invoice> returnedInvoice = invoiceRepository.findByNumber(dto.getNumber());
            if (returnedInvoice.isPresent()) throw new EntityAlreadyExistsException(Invoice.class);
            customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException(Customer.class, dto.getCustomerId()));
            invoice = InvoiceMapper.mapToInvoice(dto);
            invoice.setCustomer(customer);
            invoice = invoiceRepository.save(invoice);
            if (invoice.getId() == null) throw new Exception("Insert error");
            log.info("Invoice inserted successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
        return invoice;
    }

    /**
     * Updates an existing invoice's data.
     *
     * <p>Steps performed:
     * <ol>
     *   <li>Verifies the invoice exists by ID, or throws {@link EntityNotFoundException}.</li>
     *   <li>Resolves the new {@link Customer} by ID, or throws {@link EntityNotFoundException}.</li>
     *   <li>Maps the {@link InvoiceUpdateDTO} to an {@link Invoice} entity and sets the customer.</li>
     *   <li>Saves the updated invoice.</li>
     * </ol>
     *
     * @param id  the ID of the invoice to update
     * @param dto the DTO containing updated invoice fields
     * @return the updated {@link Invoice} entity
     * @throws EntityNotFoundException if the invoice or specified customer does not exist
     */
    @Transactional
    @Override
    public Invoice updateInvoice(Long id, InvoiceUpdateDTO dto) throws EntityNotFoundException {
        Invoice invoice = null;
        Invoice updatedInvoice = null;
        Customer customer = null;

        try {
            invoiceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(Invoice.class, id));
            customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException(Customer.class, dto.getCustomerId()));
            invoice = InvoiceMapper.mapToInvoice(dto);
            invoice.setCustomer(customer);
            updatedInvoice = invoiceRepository.save(invoice);
            log.info("Invoice updated successfully");
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        }
        return updatedInvoice;
    }

    /**
     * Deletes an invoice by its ID.
     *
     * <p>Verifies existence, then deletes the invoice.</p>
     *
     * @param id the ID of the invoice to delete
     * @return the deleted {@link Invoice} entity
     * @throws EntityNotFoundException if no invoice exists with the given ID
     */
    @Transactional
    @Override
    public Invoice deleteInvoice(Long id) throws EntityNotFoundException {
        Invoice invoice = null;
        try {
            invoice = invoiceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(Invoice.class, id));
            invoiceRepository.deleteById(id);
            log.info("Invoice deleted successfully");
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        }
        return invoice;
    }

    /**
     * Retrieves all invoices in the system.
     *
     * @return a {@link List} of all {@link Invoice} entities
     * @throws EntityNotFoundException if no invoices are found
     */
    @Override
    public List<Invoice> getAllInvoices() throws EntityNotFoundException {
        List<Invoice> invoices = null;
        try {
            invoices = invoiceRepository.findAll();
            if (invoices.isEmpty()) throw new EntityNotFoundException(Invoice.class, 0L);
            log.info("Invoice list retrieved successfully");
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        }
        return invoices;
    }

    /**
     * Retrieves a single invoice by its ID.
     *
     * @param id the ID of the invoice to retrieve
     * @return the matching {@link Invoice} entity
     * @throws EntityNotFoundException if no invoice exists with the given ID
     */
    @Override
    public Invoice getInvoiceById(Long id) throws EntityNotFoundException {
        Invoice invoice = null;
        try {
            invoice = invoiceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(Invoice.class, id));
            log.info("Invoice got successfully");
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        }
        return invoice;
    }
}
