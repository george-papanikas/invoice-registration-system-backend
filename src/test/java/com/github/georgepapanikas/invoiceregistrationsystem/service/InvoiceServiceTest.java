package com.github.georgepapanikas.invoiceregistrationsystem.service;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceInsertDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceUpdateDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Customer;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Invoice;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.CustomerRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.InvoiceRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.EntityAlreadyExistsException;
import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
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
@DisplayName("InvoiceServiceImpl Unit Tests using an in-memory H2 database")
class InvoiceServiceIntegrationTest {

    @Autowired
    private InvoiceServiceImpl invoiceService;

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private CustomerRepository customerRepo;

    private Customer existingCustomer;

    @BeforeEach
    void setUp() {
        invoiceRepo.deleteAll();
        customerRepo.deleteAll();

        existingCustomer = new Customer();
        existingCustomer.setName("Company");
        existingCustomer.setPhone("111222333");
        existingCustomer.setEmail("company@email.com");
        existingCustomer.setVatNumber("111000001");
        existingCustomer = customerRepo.save(existingCustomer);
    }

    @Test
    @DisplayName("Inserting a valid invoice saves and returns it")
    void insertInvoice_validDto_persists() throws Exception {
        InvoiceInsertDTO dto = new InvoiceInsertDTO();
        dto.setNumber("INV100");
        dto.setDate("2025-05-01");
        dto.setStatus("Paid");
        dto.setDescription("Paid invoice");
        dto.setTotalAmount(new BigDecimal("123.45"));
        dto.setCustomerId(existingCustomer.getId());

        Invoice saved = invoiceService.insertInvoice(dto);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNumber()).isEqualTo("INV100");
        List<Invoice> all = invoiceRepo.findAll();
        assertThat(all).hasSize(1);
    }

    @Test
    @DisplayName("Inserting duplicate invoice number throws EntityAlreadyExistsException")
    void insertInvoice_duplicateNumber_throws() throws Exception {
        InvoiceInsertDTO dto1 = new InvoiceInsertDTO();
        dto1.setNumber("INV200");
        dto1.setDate("2025-05-02");
        dto1.setStatus("Paid");
        dto1.setDescription("First");
        dto1.setTotalAmount(new BigDecimal("50"));
        dto1.setCustomerId(existingCustomer.getId());
        invoiceService.insertInvoice(dto1);

        InvoiceInsertDTO dto2 = new InvoiceInsertDTO();
        dto2.setNumber("INV200");
        dto2.setDate("2025-05-03");
        dto2.setStatus("Unpaid");
        dto2.setDescription("Duplicate");
        dto2.setTotalAmount(new BigDecimal("75"));
        dto2.setCustomerId(existingCustomer.getId());

        assertThrows(EntityAlreadyExistsException.class, () -> {
            invoiceService.insertInvoice(dto2);
        });
    }

    @Test
    @DisplayName("Inserting invoice for nonexistent customer throws EntityNotFoundException")
    void insertInvoice_nonexistentCustomer_throws() {
        InvoiceInsertDTO dto = new InvoiceInsertDTO();
        dto.setNumber("INV300");
        dto.setDate("2025-05-04");
        dto.setStatus("Unknown");
        dto.setDescription("No company");
        dto.setTotalAmount(new BigDecimal("100"));
        dto.setCustomerId(999L);

        assertThrows(EntityNotFoundException.class, () -> {
            invoiceService.insertInvoice(dto);
        });
    }

    @Test
    @DisplayName("Updating an existing invoice applies changes")
    void updateInvoice_existing_succeeds() throws Exception {
        // first insert
        InvoiceInsertDTO insertDto = new InvoiceInsertDTO();
        insertDto.setNumber("INV400");
        insertDto.setDate("2025-05-05");
        insertDto.setStatus("Paid");
        insertDto.setDescription("Initial");
        insertDto.setTotalAmount(new BigDecimal("200"));
        insertDto.setCustomerId(existingCustomer.getId());
        Invoice inserted = invoiceService.insertInvoice(insertDto);

        // update
        InvoiceUpdateDTO upd = new InvoiceUpdateDTO();
        upd.setId(inserted.getId());
        upd.setNumber("INV400U");
        upd.setDate("2025-06-01");
        upd.setStatus("Paid");
        upd.setDescription("Updated");
        upd.setTotalAmount(new BigDecimal("250"));
        upd.setCustomerId(existingCustomer.getId());

        Invoice updated = invoiceService.updateInvoice(inserted.getId(), upd);
        assertThat(updated.getNumber()).isEqualTo("INV400U");
        assertThat(updated.getStatus()).isEqualTo("Paid");
    }

    @Test
    @DisplayName("Updating non-existent invoice throws EntityNotFoundException")
    void updateInvoice_notFound_throws() {
        InvoiceUpdateDTO upd = new InvoiceUpdateDTO();
        upd.setId(999L);
        upd.setNumber("XXX");
        upd.setDate("2025-06-02");
        upd.setStatus("Unknown");
        upd.setDescription("XXX not found");
        upd.setTotalAmount(BigDecimal.ZERO);
        upd.setCustomerId(existingCustomer.getId());

        assertThrows(EntityNotFoundException.class, () -> {
            invoiceService.updateInvoice(999L, upd);
        });
    }

    @Test
    @DisplayName("Updating invoice with bad customer ID throws EntityNotFoundException")
    void updateInvoice_badCustomer_throws() throws Exception {
        // insert first
        InvoiceInsertDTO dto = new InvoiceInsertDTO();
        dto.setNumber("INV500");
        dto.setDate("2025-05-06");
        dto.setStatus("Paid");
        dto.setDescription("Paid invoice");
        dto.setTotalAmount(new BigDecimal("300"));
        dto.setCustomerId(existingCustomer.getId());
        Invoice ins = invoiceService.insertInvoice(dto);

        InvoiceUpdateDTO upd = new InvoiceUpdateDTO();
        upd.setId(ins.getId());
        upd.setNumber("INV500");
        upd.setDate("2025-06-03");
        upd.setStatus("Paid");
        upd.setDescription("Bad customer");
        upd.setTotalAmount(new BigDecimal("300"));
        upd.setCustomerId(999L);

        assertThrows(EntityNotFoundException.class, () -> {
            invoiceService.updateInvoice(ins.getId(), upd);
        });
    }

    @Test
    @DisplayName("Deleting an existing invoice removes it")
    void deleteInvoice_existing_deletes() throws Exception {
        // insert then delete
        InvoiceInsertDTO dto = new InvoiceInsertDTO();
        dto.setNumber("INV600");
        dto.setDate("2025-05-07");
        dto.setStatus("Unpaid");
        dto.setDescription("To delete");
        dto.setTotalAmount(new BigDecimal("400"));
        dto.setCustomerId(existingCustomer.getId());
        Invoice ins = invoiceService.insertInvoice(dto);

        Invoice del = invoiceService.deleteInvoice(ins.getId());
        assertThat(del.getId()).isEqualTo(ins.getId());
        assertThat(invoiceRepo.existsById(ins.getId())).isFalse();
    }

    @Test
    @DisplayName("Deleting non-existent invoice throws EntityNotFoundException")
    void deleteInvoice_notFound_throws() {
        assertThrows(EntityNotFoundException.class, () -> {
            invoiceService.deleteInvoice(999L);
        });
    }

    @Test
    @DisplayName("Retrieving all invoices returns list when data exists")
    void getAllInvoices_withData_returnsList() throws Exception {
        // insert two invoices
        for (int i = 1; i <= 2; i++) {
            InvoiceInsertDTO dto = new InvoiceInsertDTO();
            dto.setNumber("INV" + i);
            dto.setDate("2025-05-08");
            dto.setStatus("Paid");
            dto.setDescription("Paid invoice");
            dto.setTotalAmount(new BigDecimal("10"));
            dto.setCustomerId(existingCustomer.getId());
            invoiceService.insertInvoice(dto);
        }
        List<Invoice> all = invoiceService.getAllInvoices();
        assertThat(all).hasSize(2);
    }

    @Test
    @DisplayName("Retrieving all invoices when empty throws EntityNotFoundException")
    void getAllInvoices_empty_throws() {
        assertThrows(EntityNotFoundException.class, () -> {
            invoiceService.getAllInvoices();
        });
    }

    @Test
    @DisplayName("Retrieving invoice by ID returns the invoice when it exists")
    void getInvoiceById_existing_returnsInvoice() throws Exception {
        // insert
        InvoiceInsertDTO dto = new InvoiceInsertDTO();
        dto.setNumber("INV700");
        dto.setDate("2025-05-09");
        dto.setStatus("Unpaid");
        dto.setDescription("Unpaid invoice");
        dto.setTotalAmount(new BigDecimal("500"));
        dto.setCustomerId(existingCustomer.getId());
        Invoice ins = invoiceService.insertInvoice(dto);

        Invoice fetched = invoiceService.getInvoiceById(ins.getId());
        assertThat(fetched.getNumber()).isEqualTo("INV700");
    }

    @Test
    @DisplayName("Retrieving invoice by non-existent ID throws EntityNotFoundException")
    void getInvoiceById_notFound_throws() {
        assertThrows(EntityNotFoundException.class, () -> {
            invoiceService.getInvoiceById(999L);
        });
    }
}