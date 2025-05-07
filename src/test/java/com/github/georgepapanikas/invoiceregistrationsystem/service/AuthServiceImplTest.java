package com.github.georgepapanikas.invoiceregistrationsystem.service;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.JWTAuthResponseDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.LoginDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.RegisterDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Role;
import com.github.georgepapanikas.invoiceregistrationsystem.model.User;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.RoleRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.UserRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.APIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Transactional
@DisplayName("AuthServiceImpl Unit Tests using an in-memory H2 database")
class AuthServiceIntegrationTest {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Seed the roles table before each test.
     */
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        // Ensure default role exists
        roleRepository.save(new Role(null, "ROLE_USER"));
    }

    @Test
    @DisplayName("Register new user successfully")
    void register_newUser_succeeds() {
        RegisterDTO dto = new RegisterDTO();
        dto.setName("George");
        dto.setUsername("george");
        dto.setEmail("george@email.com");
        dto.setPassword("1234");

        String msg = authService.register(dto);
        assertEquals("User George successfully registered", msg);

        // Verify persisted user
        User user = userRepository.findByUsernameOrEmail("george", "george@email.com").orElseThrow();
        assertEquals("George", user.getName());
        assertEquals("george@email.com", user.getEmail());
        // Password should be encoded
        assertNotEquals("1234", user.getPassword());
        assertTrue(passwordEncoder.matches("1234", user.getPassword()));
        // Check default role
        assertTrue(user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Register with duplicate username fails")
    void register_duplicateUsername_throws() {
        // Pre-insert a user with username 'maria'
        User existing = new User();
        existing.setName("Maria");
        existing.setUsername("maria");
        existing.setEmail("maria@email.com");
        existing.setPassword(passwordEncoder.encode("4321"));
        existing.setRoles(Set.of(roleRepository.findByName("ROLE_USER")));
        userRepository.save(existing);

        RegisterDTO dto = new RegisterDTO();
        dto.setName("Anna");
        dto.setUsername("maria");
        dto.setEmail("anna@email.com");
        dto.setPassword("9876");

        APIException ex = assertThrows(APIException.class, () -> authService.register(dto));
        assertEquals("Username maria is already in use", ex.getMessage());
    }

    @Test
    @DisplayName("Register with duplicate email fails")
    void register_duplicateEmail_throws() {
        // Pre-insert a user with email 'bob@email.com'
        User existing = new User();
        existing.setName("Bob");
        existing.setUsername("bob");
        existing.setEmail("bob@email.com");
        existing.setPassword(passwordEncoder.encode("1234"));
        existing.setRoles(Set.of(roleRepository.findByName("ROLE_USER")));
        userRepository.save(existing);

        RegisterDTO dto = new RegisterDTO();
        dto.setName("Alice");
        dto.setUsername("alice");
        dto.setEmail("bob@email.com");
        dto.setPassword("4321");

        APIException ex = assertThrows(APIException.class, () -> authService.register(dto));
        assertEquals("Email bob@email.com is already in use", ex.getMessage());
    }

    @Test
    @DisplayName("Login with correct credentials returns JWTAuthResponse")
    void login_validCredentials_succeeds() {
        // First register a user via service
        RegisterDTO reg = new RegisterDTO();
        reg.setName("Jason");
        reg.setUsername("jason");
        reg.setEmail("jason@email.com");
        reg.setPassword("9876");
        authService.register(reg);

        LoginDTO login = new LoginDTO();
        login.setUsernameOrEmail("jason");
        login.setPassword("9876");

        JWTAuthResponseDTO resp = authService.login(login);
        assertNotNull(resp.getAccessToken());
        assertEquals("ROLE_USER", resp.getRole());
    }

    @Test
    @DisplayName("Login with wrong password throws BadCredentialsException")
    void login_wrongPassword_throws() {
        // Register user
        RegisterDTO reg = new RegisterDTO();
        reg.setName("George");
        reg.setUsername("george");
        reg.setEmail("george@email.com");
        reg.setPassword("1235");
        authService.register(reg);

        LoginDTO login = new LoginDTO();
        login.setUsernameOrEmail("george");
        login.setPassword("1236");

        assertThrows(BadCredentialsException.class, () -> authService.login(login));
    }
}
