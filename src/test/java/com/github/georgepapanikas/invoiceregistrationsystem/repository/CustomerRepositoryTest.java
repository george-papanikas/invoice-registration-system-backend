package com.github.georgepapanikas.invoiceregistrationsystem.repository;

import com.github.georgepapanikas.invoiceregistrationsystem.model.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CustomerRepository Unit Tests using an in-memory H2 database")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("When saving a customer with a unique VAT number, it should be persisted")
    void saveCustomer_withUniqueVat_persists() {

        Customer c = new Customer();
        c.setName("Company1");
        c.setPhone("1234567890"); // ten-digit string
        c.setEmail("company1@email.com");
        c.setVatNumber("123456789");  // nine-digit string

        Customer saved = customerRepository.saveAndFlush(c);

        assertThat(saved.getId()).isNotNull();
        Optional<Customer> fetched = customerRepository.findByVatNumber("123456789");
        assertThat(fetched)
                .isPresent()
                .get()
                .extracting(Customer::getEmail)
                .isEqualTo("company1@email.com");
    }

    @Test
    @DisplayName("findByVatNumber should return empty when no customer exists")
    void findByVatNumber_whenNotFound_returnsEmpty() {
        Optional<Customer> result = customerRepository.findByVatNumber("000000000");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Saving two customers with the same VAT number should fail")
    void saveCustomer_duplicateVat_throwsException() {

        Customer first = new Customer();
        first.setName("Company2");
        first.setPhone("0987654321");
        first.setEmail("company2@email.com");
        first.setVatNumber("987654321");
        customerRepository.saveAndFlush(first);

        Customer duplicate = new Customer();
        duplicate.setName("Company3");
        duplicate.setPhone("5555555555");
        duplicate.setEmail("company3@email.com");
        duplicate.setVatNumber("987654321");

        assertThrows(DataIntegrityViolationException.class, () -> {
            customerRepository.saveAndFlush(duplicate);
        });
    }
}