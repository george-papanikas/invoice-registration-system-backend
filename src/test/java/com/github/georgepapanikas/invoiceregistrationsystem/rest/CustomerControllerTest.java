package com.github.georgepapanikas.invoiceregistrationsystem.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.CustomerInsertDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.CustomerUpdateDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Customer;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Invoice;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.CustomerRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.InvoiceRepository;
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
@DisplayName("CustomerController Unit Tests using an in-memory H2 database and MockMvc")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer existing;

    @BeforeEach
    void setUp() {
        // clear any leftovers, then insert one customer
        invoiceRepo.deleteAll();
        customerRepo.deleteAll();

        existing = new Customer();
        existing.setName("Company");
        existing.setPhone("0123456789");
        existing.setEmail("company@email.com");
        existing.setVatNumber("999999999");
        existing = customerRepo.save(existing);
    }

    @Test
    @DisplayName("GET all customers returns 400 when none exist")
    void getAll_noCustomers_returns400() throws Exception {
        customerRepo.deleteAll();

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET all customers returns 200 and list when customers exist")
    void getAll_withCustomers_returns200AndList() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(existing.getId()))
                .andExpect(jsonPath("$[0].name").value("Company"))
                .andExpect(jsonPath("$[0].vatNumber").value("999999999"));
    }

    @Test
    @DisplayName("GET customer by ID returns 404 when not found")
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/customers/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET customer by ID returns 200 and body when found")
    void getById_found_returns200AndBody() throws Exception {
        mockMvc.perform(get("/api/customers/{id}", existing.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existing.getId()))
                .andExpect(jsonPath("$.email").value("company@email.com"));
    }

    @Test
    @DisplayName("POST new customer returns 201, sets Location header, and persists data")
    void insert_validDto_returns201AndLocation() throws Exception {
        CustomerInsertDTO dto = new CustomerInsertDTO();
        dto.setName("New Company");
        dto.setPhone("5555500000");
        dto.setEmail("newcompany@email.com");
        dto.setVatNumber("123456789");

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/customers/")))
                .andExpect(jsonPath("$.name").value("New Company"))
                .andExpect(jsonPath("$.vatNumber").value("123456789"));

        // DB side-effect
        List<Customer> all = customerRepo.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    @DisplayName("PUT existing customer returns 200 and updates fields")
    void update_existing_returns200AndUpdated() throws Exception {
        CustomerUpdateDTO dto = new CustomerUpdateDTO();
        dto.setId(existing.getId());
        dto.setName("Updated Company");
        dto.setPhone("9876543210");
        dto.setEmail("updatedcompany@email.com");
        dto.setVatNumber("999999999");

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/customers/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Company"))
                .andExpect(jsonPath("$.email").value("updatedcompany@email.com"));
    }

    @Test
    @DisplayName("PUT non-existent customer returns 404")
    void update_notFound_returns404() throws Exception {
        CustomerUpdateDTO dto = new CustomerUpdateDTO();
        dto.setId(9999L);
        dto.setName("Not Found");
        dto.setPhone("0009997778");
        dto.setEmail("notfound@email.com");
        dto.setVatNumber("123456781");

        mockMvc.perform(put("/api/customers/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE non-existent customer returns 404")
    void delete_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/customers/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE customer with invoices returns 400 and error message")
    void delete_withInvoices_returns400() throws Exception {
        // create invoice tied to 'existing'
        Invoice inv = new Invoice();
        inv.setNumber("INV400");
        inv.setDate("2025-04-20");
        inv.setStatus("Open");
        inv.setDescription("Open invoice");
        inv.setTotalAmount(new BigDecimal("99.99"));
        inv.setCustomer(existing);
        invoiceRepo.save(inv);

        mockMvc.perform(delete("/api/customers/{id}", existing.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot delete customer because related invoices exist"));
    }

    @Test
    @DisplayName("DELETE customer with no invoices returns 200 and deleted entity")
    void delete_noInvoices_returns200AndBody() throws Exception {
        mockMvc.perform(delete("/api/customers/{id}", existing.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existing.getId()))
                .andExpect(jsonPath("$.name").value("Company"));
    }
}
