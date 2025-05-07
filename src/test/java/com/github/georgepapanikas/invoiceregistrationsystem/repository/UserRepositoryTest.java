package com.github.georgepapanikas.invoiceregistrationsystem.repository;

import com.github.georgepapanikas.invoiceregistrationsystem.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.HashSet;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Unit Tests using an in-memory H2 database")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("findByUsername returns empty when no user exists")
    void findByUsername_nonExisting_returnsEmpty() {
        Optional<User> result = userRepository.findByUsername("no_user");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUsername returns saved user")
    void findByUsername_existing_returnsUser() {
        User u = new User();
        u.setName("User");
        u.setUsername("user");
        u.setEmail("user@email.com");
        u.setPassword("password");
        u.setRoles(new HashSet<>());
        userRepository.save(u);

        Optional<User> found = userRepository.findByUsername("user");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user@email.com");
    }

    @Test
    @DisplayName("findByUsernameOrEmail finds by username or email")
    void findByUsernameOrEmail_findsCorrectly() {
        User u = new User();
        u.setName("Anna");
        u.setUsername("anna");
        u.setEmail("anna@email.com");
        u.setPassword("9994");
        u.setRoles(new HashSet<>());
        userRepository.save(u);

        Optional<User> byUsername = userRepository.findByUsernameOrEmail("anna", "anna");
        Optional<User> byEmail = userRepository.findByUsernameOrEmail("anna@email.com", "anna@email.com");

        assertThat(byUsername).isPresent().contains(u);
        assertThat(byEmail).isPresent().contains(u);
    }

    @Test
    @DisplayName("existsByUsername returns true/false correctly")
    void existsByUsername_works() {
        assertThat(userRepository.existsByUsername("george")).isFalse();
        User u = new User();
        u.setName("George");
        u.setUsername("george");
        u.setEmail("george@email.com");
        u.setPassword("7897");
        u.setRoles(new HashSet<>());
        userRepository.save(u);

        assertThat(userRepository.existsByUsername("george")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail returns true/false correctly")
    void existsByEmail_works() {
        assertThat(userRepository.existsByEmail("maria@email.com")).isFalse();
        User u = new User();
        u.setName("Maria");
        u.setUsername("maria");
        u.setEmail("maria@email.com");
        u.setPassword("3690");
        u.setRoles(new HashSet<>());
        userRepository.save(u);

        assertThat(userRepository.existsByEmail("maria@email.com")).isTrue();
    }
}