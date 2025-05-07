package com.github.georgepapanikas.invoiceregistrationsystem.service;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.CustomerInsertDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.CustomerUpdateDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Customer;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Invoice;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.CustomerRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.InvoiceRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@DisplayName("CustomerServiceImpl Unit Tests using an in-memory H2 database")
class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private InvoiceRepository invoiceRepo;

    private Customer existing;

    @BeforeEach
    void setUp() {
        invoiceRepo.deleteAll();
        customerRepo.deleteAll();

        existing = new Customer();
        existing.setName("Company");
        existing.setPhone("123456789");
        existing.setEmail("company@email.com");
        existing.setVatNumber("000123456");
        existing = customerRepo.save(existing);
    }

    @Test
    @DisplayName("Insert valid customer DTO should persist a new customer")
    void insertCustomer_validDto_persists() throws Exception {
        CustomerInsertDTO dto = new CustomerInsertDTO();
        dto.setName("Company1");
        dto.setPhone("987654321");
        dto.setEmail("company1@email.com");
        dto.setVatNumber("000654321");

        Customer saved = customerService.insertCustomer(dto);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Company1");

        List<Customer> all = customerRepo.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    @DisplayName("Get all customers when data exists returns list")
    void getAllCustomers_withData_returnsList() throws EntityNotFoundException {
        List<Customer> list = customerService.getAllCustomers();
        assertThat(list).hasSize(1);
    }

    @Test
    @DisplayName("Get all customers when no data throws EntityNotFoundException")
    void getAllCustomers_empty_throws() {
        customerRepo.deleteAll();
        assertThrows(EntityNotFoundException.class, () -> {
            customerService.getAllCustomers();
        });
    }

    @Test
    @DisplayName("Get existing customer by ID returns the correct entity")
    void getCustomerById_found_returnsCustomer() throws EntityNotFoundException {
        Customer fetched = customerService.getCustomerById(existing.getId());
        assertThat(fetched.getEmail()).isEqualTo("company@email.com");
    }

    @Test
    @DisplayName("Get customer by non-existent ID throws EntityNotFoundException")
    void getCustomerById_notFound_throws() {
        assertThrows(EntityNotFoundException.class, () -> {
            customerService.getCustomerById(999L);
        });
    }

    @Test
    @DisplayName("Update existing customer applies updates correctly")
    void updateCustomer_existing_appliesUpdates() throws EntityNotFoundException {
        // prepare an update DTO
        CustomerUpdateDTO dto = new CustomerUpdateDTO();
        dto.setId(existing.getId());
        dto.setName("Company Updated");
        dto.setPhone("111222333");
        dto.setEmail("companyupdated@email.com");

        Customer updated = customerService.updateCustomer(existing.getId(), dto);

        // verify returned entity
        assertThat(updated.getId()).isEqualTo(existing.getId());
        assertThat(updated.getName()).isEqualTo("Company Updated");
        assertThat(updated.getEmail()).isEqualTo("companyupdated@email.com");

        // verify database state
        Customer fromDb = customerRepo.findById(existing.getId()).orElseThrow();
        assertThat(fromDb.getName()).isEqualTo("Company Updated");
        assertThat(fromDb.getPhone()).isEqualTo("111222333");
    }

    @Test
    @DisplayName("Update non-existent customer ID throws EntityNotFoundException")
    void updateCustomer_notFound_throwsEntityNotFound() {
        CustomerUpdateDTO dto = new CustomerUpdateDTO();
        dto.setId(999L);
        dto.setName("NotFound");
        dto.setPhone("000");
        dto.setEmail("notfound@email.com");

        assertThrows(EntityNotFoundException.class, () -> {
            customerService.updateCustomer(999L, dto);
        });
    }

    @Test
    @DisplayName("Delete customer without invoices succeeds")
    void deleteCustomer_noInvoices_deletes() throws EntityNotFoundException {
        Customer deleted = customerService.deleteCustomer(existing.getId());
        assertThat(deleted.getId()).isEqualTo(existing.getId());
        assertThat(customerRepo.existsById(existing.getId())).isFalse();
    }

    @Test
    @DisplayName("Delete non-existent customer ID throws EntityNotFoundException")
    void deleteCustomer_notFound_throws() {
        assertThrows(EntityNotFoundException.class, () -> {
            customerService.deleteCustomer(999L);
        });
    }

    @Test
    @DisplayName("Delete customer with existing invoices throws DataIntegrityViolationException")
    void deleteCustomer_withInvoices_throwsDataIntegrity() {
        Invoice inv = new Invoice();
        inv.setNumber("INV001");
        inv.setDate("2025-04-30");
        inv.setStatus("Paid");
        inv.setDescription("New invoice");
        inv.setTotalAmount(new BigDecimal("100.00"));
        inv.setCustomer(existing);
        invoiceRepo.saveAndFlush(inv);

        assertThrows(DataIntegrityViolationException.class, () -> {
            customerService.deleteCustomer(existing.getId());
        });
    }
}
