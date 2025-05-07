package com.github.georgepapanikas.invoiceregistrationsystem.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceInsertDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.InvoiceUpdateDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Customer;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Invoice;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.CustomerRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.InvoiceRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.service.IInvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockUser(username="tester", roles={"ADMIN","USER"})
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Transactional
@DisplayName("InvoiceController Unit Tests using an in-memory H2 database and MockMvc")
class InvoiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private IInvoiceService invoiceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer existingCustomer;

    @BeforeEach
    void setUp() {
        invoiceRepo.deleteAll();
        customerRepo.deleteAll();

        existingCustomer = new Customer();
        existingCustomer.setName("Company");
        existingCustomer.setPhone("8001234112");
        existingCustomer.setEmail("company@email.com");
        existingCustomer.setVatNumber("111000222");
        existingCustomer = customerRepo.save(existingCustomer);
    }

    @Test
    @DisplayName("GET /api/invoices with no invoices returns 400 Bad Request")
    void getAll_noInvoices_returns400() throws Exception {
        mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/invoices with existing invoices returns 200 OK and JSON list")
    void getAll_withInvoices_returns200AndList() throws Exception {
        InvoiceInsertDTO dto = new InvoiceInsertDTO();
        dto.setNumber("INV01");
        dto.setDate("2025-06-01");
        dto.setStatus("Unpaid");
        dto.setDescription("Unpaid invoice");
        dto.setTotalAmount(new BigDecimal("100"));
        dto.setCustomerId(existingCustomer.getId());
        invoiceService.insertInvoice(dto);

        mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].number").value("INV01"))
                .andExpect(jsonPath("$[0].customerId").value(existingCustomer.getId()));
    }

    @Test
    @DisplayName("GET /api/invoices/{id} for non-existent invoice returns 404 Not Found")
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/invoices/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/invoices/{id} for existing invoice returns 200 OK and JSON body")
    void getById_found_returns200AndBody() throws Exception {
        Invoice ins = invoiceService.insertInvoice(new InvoiceInsertDTO() {{
            setNumber("INV02");
            setDate("2025-06-02");
            setStatus("Paid");
            setDescription("Paid invoice");
            setTotalAmount(new BigDecimal("200"));
            setCustomerId(existingCustomer.getId());
        }});

        mockMvc.perform(get("/api/invoices/{id}", ins.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ins.getId()))
                .andExpect(jsonPath("$.number").value("INV02"));
    }

    @Test
    @DisplayName("POST /api/invoices with valid data returns 201 Created, Location header, and JSON body")
    void insert_validDto_returns201AndLocation() throws Exception {
        InvoiceInsertDTO dto = new InvoiceInsertDTO();
        dto.setNumber("INV03");
        dto.setDate("2025-08-08");
        dto.setStatus("Unpaid");
        dto.setDescription("Unpaid invoice");
        dto.setTotalAmount(new BigDecimal("300"));
        dto.setCustomerId(existingCustomer.getId());

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/invoices/")))
                .andExpect(jsonPath("$.number").value("INV03"));

        List<Invoice> all = invoiceRepo.findAll();
        assertThat(all).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/invoices with invalid data returns 503 Service Unavailable")
    void insert_invalidDto_returns503() throws Exception {
        String badJson = objectMapper.writeValueAsString(new InvoiceInsertDTO());

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson)
                )
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("PUT /api/invoices/{id} for existing invoice returns 200 OK and updated JSON")
    void update_existing_returns200AndUpdated() throws Exception {
        Invoice ins = invoiceService.insertInvoice(new InvoiceInsertDTO() {{
            setNumber("INV04");
            setDate("2025-06-04");
            setStatus("Unpaid");
            setDescription("Unpaid invoice");
            setTotalAmount(new BigDecimal("400"));
            setCustomerId(existingCustomer.getId());
        }});

        InvoiceUpdateDTO upd = new InvoiceUpdateDTO();
        upd.setId(ins.getId());
        upd.setNumber("INV044");
        upd.setDate("2025-07-04");
        upd.setStatus("Paid");
        upd.setDescription("Paid invoice");
        upd.setTotalAmount(new BigDecimal("450"));
        upd.setCustomerId(existingCustomer.getId());

        String json = objectMapper.writeValueAsString(upd);

        mockMvc.perform(put("/api/invoices/{id}", ins.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("INV044"))
                .andExpect(jsonPath("$.status").value("Paid"));
    }

    @Test
    @DisplayName("PUT /api/invoices/{id} for non-existent invoice returns 404 Not Found")
    void update_notFound_returns404() throws Exception {
        InvoiceUpdateDTO upd = new InvoiceUpdateDTO();
        upd.setId(9999L);
        upd.setNumber("INV00000");
        upd.setDate("2025-07-02");
        upd.setStatus("Not found");
        upd.setDescription("Not found invoice");
        upd.setTotalAmount(BigDecimal.ZERO);
        upd.setCustomerId(existingCustomer.getId());

        mockMvc.perform(put("/api/invoices/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upd))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/invoices/{id} for non-existent invoice returns 404 Not Found")
    void delete_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/invoices/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/invoices/{id} for existing invoice returns 200 OK and JSON body of deleted")
    void delete_existing_returns200AndBody() throws Exception {
        Invoice ins = invoiceService.insertInvoice(new InvoiceInsertDTO() {{
            setNumber("INV05");
            setDate("2025-10-01");
            setStatus("Unpaid");
            setDescription("Unpaid invoice");
            setTotalAmount(new BigDecimal("500"));
            setCustomerId(existingCustomer.getId());
        }});

        mockMvc.perform(delete("/api/invoices/{id}", ins.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ins.getId()))
                .andExpect(jsonPath("$.number").value("INV05"));
    }
}