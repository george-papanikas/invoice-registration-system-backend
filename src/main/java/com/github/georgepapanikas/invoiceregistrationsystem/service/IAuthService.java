package com.github.georgepapanikas.invoiceregistrationsystem.service;

import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.APIException;
import org.springframework.security.core.AuthenticationException;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.JWTAuthResponseDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.LoginDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.RegisterDTO;

/**
 * Service interface defining authentication operations.
 *
 * <p>Implementations should handle user registration and login,
 * including validation, password encoding, and JWT token issuance.</p>
 */
public interface IAuthService {

    /**
     * Registers a new user in the system.
     *
     * <p>Checks for unique username/email, encodes the password,
     * assigns default roles, and persists the user.</p>
     *
     * @param registerDto DTO containing the user's registration details:
     *                    name, username, email, and password
     * @return a confirmation message upon successful registration
     * @throws APIException if the username or email is already in use
     */
    String register(RegisterDTO registerDto);

    /**
     * Authenticates a user and issues a JWT authentication response.
     *
     * <p>Validates credentials, generates a JWT token, and retrieves
     * the user's role for inclusion in the response.</p>
     *
     * @param loginDto DTO containing login credentials:
     *                 username or email, and password
     * @return a {@link JWTAuthResponseDTO} containing the access token and assigned role
     * @throws AuthenticationException if authentication fails due to invalid credentials
     */
    JWTAuthResponseDTO login(LoginDTO loginDto);
}
