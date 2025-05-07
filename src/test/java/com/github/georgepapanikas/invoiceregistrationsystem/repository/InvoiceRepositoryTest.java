package com.github.georgepapanikas.invoiceregistrationsystem.repository;

import com.github.georgepapanikas.invoiceregistrationsystem.model.Customer;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Invoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("InvoiceRepository Unit Tests using an in-memory H2 database")
class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private CustomerRepository customerRepo;

    private Customer customer;

    @BeforeEach
    void setUp() {
        // create a Customer to satisfy the FK on Invoice
        customer = new Customer();
        customer.setName("Company");
        customer.setPhone("8005550000");
        customer.setEmail("company@email.com");
        customer.setVatNumber("999123456");
        customer = customerRepo.saveAndFlush(customer);
    }

    @Test
    @DisplayName("Saving an invoice with a unique number should persist")
    void saveInvoice_uniqueNumber_persists() {
        Invoice inv = new Invoice();
        inv.setNumber("INV001");
        inv.setDate("2025-07-01");
        inv.setStatus("New");
        inv.setDescription("First invoice");
        inv.setTotalAmount(new BigDecimal("150.00"));
        inv.setCustomer(customer);

        Invoice saved = invoiceRepo.saveAndFlush(inv);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNumber()).isEqualTo("INV001");
    }

    @Test
    @DisplayName("Saving a second invoice with the same number should fail")
    void saveInvoice_duplicateNumber_throwsException() {
        // first one
        Invoice first = new Invoice();
        first.setNumber("INV002");
        first.setDate("2025-07-02");
        first.setStatus("New");
        first.setDescription("Second invoice");
        first.setTotalAmount(new BigDecimal("200.00"));
        first.setCustomer(customer);
        invoiceRepo.saveAndFlush(first);

        // duplicate
        Invoice dup = new Invoice();
        dup.setNumber("INV002");
        dup.setDate("2025-09-03");
        dup.setStatus("Paid");
        dup.setDescription("Duplicate invoice");
        dup.setTotalAmount(new BigDecimal("250.00"));
        dup.setCustomer(customer);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invoiceRepo.saveAndFlush(dup);
        });
    }

    @Test
    @DisplayName("findByNumber should return empty when no invoice exists")
    void findByNumber_notFound_returnsEmpty() {
        Optional<Invoice> opt = invoiceRepo.findByNumber("000000");
        assertThat(opt).isEmpty();
    }

    @Test
    @DisplayName("findByNumber should return the matching invoice when it exists")
    void findByNumber_found_returnsInvoice() {
        Invoice inv = new Invoice();
        inv.setNumber("INV003");
        inv.setDate("2025-12-11");
        inv.setStatus("New");
        inv.setDescription("Third invoice");
        inv.setTotalAmount(new BigDecimal("300.00"));
        inv.setCustomer(customer);
        invoiceRepo.saveAndFlush(inv);

        Optional<Invoice> opt = invoiceRepo.findByNumber("INV003");
        assertThat(opt).isPresent()
                .get()
                .extracting(Invoice::getDescription)
                .isEqualTo("Third invoice");
    }
}