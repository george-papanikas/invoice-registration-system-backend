package com.github.georgepapanikas.invoiceregistrationsystem.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.JWTAuthResponseDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.LoginDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.RegisterDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Transactional
@DisplayName("AuthController Unit Tests using an in-memory H2 database and MockMvc")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Full register-then-login flow should succeed with valid credentials")
    void register_thenLogin_flow_works() throws Exception {
        // Register a new user
        RegisterDTO reg = new RegisterDTO();
        reg.setName("New User");
        reg.setUsername("new_user");
        reg.setEmail("newuser@email.com");
        reg.setPassword("1234");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg))
                )
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("registered")));

        // Now login with the same credentials
        LoginDTO login = new LoginDTO();
        login.setUsernameOrEmail("new_user");
        login.setPassword("1234");

        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Deserialize and do extra sanity checks
        JWTAuthResponseDTO resp = objectMapper.readValue(body, JWTAuthResponseDTO.class);
        assertThat(resp.getAccessToken()).startsWith("eyJ");  // typical JWT header
    }

    @Test
    @DisplayName("Register endpoint should return 400 Bad Request for invalid payload")
    void register_withInvalidDto_returns400() throws Exception {
        // Bean validation should reject when password is missing
        RegisterDTO bad = new RegisterDTO();
        bad.setName("No Password");
        bad.setUsername("no_password");
        bad.setEmail("nopassword@email.com");
        // bad.setPassword(null) or bad.setPassword("");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login endpoint should return 401 Unauthorized for wrong credentials")
    void login_withWrongPassword_returns401() throws Exception {
        // First register so the user exists
        RegisterDTO reg = new RegisterDTO();
        reg.setName("Bob");
        reg.setUsername("bob");
        reg.setEmail("bob@email.com");
        reg.setPassword("4321");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg))
        ).andExpect(status().isCreated());

        // Then attempt login with bad password
        LoginDTO login = new LoginDTO();
        login.setUsernameOrEmail("bob");
        login.setPassword("4312");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))
                )
                // by controller’s advice current logic, a login failure throws and is caught as 401
                .andExpect(status().isUnauthorized());
    }
}