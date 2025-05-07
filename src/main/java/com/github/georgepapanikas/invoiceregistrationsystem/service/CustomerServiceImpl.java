package com.github.georgepapanikas.invoiceregistrationsystem.service;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.CustomerInsertDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.CustomerUpdateDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.mapper.CustomerMapper;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Customer;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.CustomerRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.InvoiceRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * {@link ICustomerService} implementation handling customer lifecycle operations.
 * <p>
 * Uses {@link CustomerRepository} for persistence and {@link InvoiceRepository}
 * to guard against deleting customers with existing invoices. Applies
 * transactional boundaries and logs each operation.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;

    /**
     * Inserts a new customer based on the given DTO.
     *
     * <p>Maps the {@link CustomerInsertDTO} to a {@link Customer} entity,
     * saves it, and verifies that an ID was generated.</p>
     *
     * @param dto the data transfer object containing new customer details
     * @return the persisted {@link Customer} with a generated ID
     * @throws Exception if persistence fails or the generated ID is null
     */
    @Transactional
    @Override
    public Customer insertCustomer(CustomerInsertDTO dto) throws Exception {
        Customer savedCustomer = null;
        try {
            savedCustomer = customerRepository.save(CustomerMapper.mapToCustomer(dto));
            if (savedCustomer.getId() == null) throw new Exception("Customer Insert Error");
            log.info("Customer inserted successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
        return savedCustomer;
    }

    /**
     * Updates an existing customer's data.
     *
     * <p>Verifies the customer exists by ID, applies updates from
     * {@link CustomerUpdateDTO}, and saves the changes.</p>
     *
     * @param id  the ID of the customer to update
     * @param dto the DTO containing updated customer fields
     * @return the updated {@link Customer} entity
     * @throws EntityNotFoundException if no customer with the given ID exists
     */
    @Transactional
    @Override
    public Customer updateCustomer(Long id, CustomerUpdateDTO dto) throws EntityNotFoundException {
        Customer updatedCustomer = null;
        try {
            customerRepository.findById(id)
                    .orElseThrow(() ->  new EntityNotFoundException(Customer.class, dto.getId()));
            updatedCustomer = customerRepository.save(CustomerMapper.mapToCustomer(dto));
            log.info("Customer updated successfully");

        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        }

        return updatedCustomer;
    }

    /**
     * Deletes a customer by ID.
     *
     * <p>Ensures the customer exists, then attempts deletion. If the customer
     * has related invoices, a {@link DataIntegrityViolationException} is thrown.</p>
     *
     * @param id the ID of the customer to delete
     * @return the deleted {@link Customer} entity for confirmation
     * @throws EntityNotFoundException         if no customer with the given ID exists
     * @throws DataIntegrityViolationException if the customer cannot be deleted
     *                                        due to existing invoice references
     */
    @Transactional
    @Override
    public Customer deleteCustomer(Long id) throws EntityNotFoundException, DataIntegrityViolationException {
        Customer customer = null;
        Long invoiceCount = 0L;
        try {
            customer = customerRepository.findById(id)
                    .orElseThrow(() ->  new EntityNotFoundException(Customer.class, id));
            invoiceCount = invoiceRepository.countByCustomerId(id);
            if (invoiceCount > 0) {
                throw new DataIntegrityViolationException("Customer has already been invoked");
            }
            customerRepository.deleteById(id);
            customerRepository.flush();
            log.info("Customer deleted successfully");
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        } catch (ConstraintViolationException | DataIntegrityViolationException ex){
            log.error("Cannot delete customer with ID {}: They have existing invoices", id);
            throw ex;
        }
        return customer;
    }

    /**
     * Retrieves all customers in the system.
     *
     * @return a {@link List} of all {@link Customer} entities
     * @throws EntityNotFoundException if no customers are found
     */
    @Override
    public List<Customer> getAllCustomers() throws EntityNotFoundException {
        List<Customer> customers = null;
        try {
            customers = customerRepository.findAll();
            if (customers.isEmpty()) throw new EntityNotFoundException(Customer.class, 0L);
            log.info("Customers found");
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        }
        return customers;
    }

    /**
     * Retrieves a single customer by ID.
     *
     * @param id the ID of the customer to retrieve
     * @return the matching {@link Customer} entity
     * @throws EntityNotFoundException if no customer with the given ID exists
     */
    @Override
    public Customer getCustomerById(Long id) throws EntityNotFoundException {
        Customer customer = null;
        try {
            customer = customerRepository.findById(id)
                            .orElseThrow(() ->  new EntityNotFoundException(Customer.class, id));
            log.info("Customer found");
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        }
        return customer;
    }
}
