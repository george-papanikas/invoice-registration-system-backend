package com.github.georgepapanikas.invoiceregistrationsystem.rest;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.JWTAuthResponseDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.LoginDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.RegisterDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling authentication-related endpoints:
 * user registration and login. All endpoints are prefixed with "/api/auth"
 * and allow cross-origin requests from any origin.
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    /**
     * Service layer interface for authentication operations
     * such as registering new users and authenticating existing ones.
     */
    private IAuthService authService;

    /**
     * Registers a new user in the system.
     *
     * <p>Accepts a {@link RegisterDTO} containing the user's name, username,
     * email, and password, and returns a confirmation message.</p>
     *
     * @param registerDto the DTO with registration details
     * @return a {@link ResponseEntity} containing a success message and
     *         {@link HttpStatus#CREATED} if registration is successful
     */
    @Operation(
            summary     = "Register a new user",
            description = "Creates a new user account with name, username, email, and password.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "JSON payload containing name, username, email and password for the new user",
                    required    = true,
                    content     = @Content(
                            mediaType = "application/json",
                            schema    = @Schema(implementation = RegisterDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description  = "User registered successfully",
                            content      = @Content(
                                    mediaType = "text/plain",
                                    schema    = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description  = "Invalid registration details"
                    )
            }
    )
    @PostMapping(("/register"))
    public ResponseEntity<String> register(@Valid @org.springframework.web.bind.annotation.RequestBody RegisterDTO registerDto) {
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and issues a JWT access token.
     *
     * <p>Accepts a {@link LoginDTO} with username/email and password,
     * and returns a {@link JWTAuthResponseDTO} containing the token and role.</p>
     *
     * @param loginDto the DTO with login credentials
     * @return a {@link ResponseEntity} containing the JWTAuthResponse and
     *         {@link HttpStatus#OK} if authentication is successful
     */
    @Operation(
            summary     = "User login",
            description = "Authenticates user credentials and returns a JWT access token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "JSON payload containing username/email and password",
                    required    = true,
                    content     = @Content(
                            mediaType = "application/json",
                            schema    = @Schema(implementation = LoginDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description  = "Authentication successful",
                            content      = @Content(
                                    mediaType = "application/json",
                                    schema    = @Schema(implementation = JWTAuthResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description  = "Invalid username or password"
                    )
            }
    )
    @PostMapping(("/login"))
    public ResponseEntity<JWTAuthResponseDTO> login(@Valid @org.springframework.web.bind.annotation.RequestBody LoginDTO loginDto) {
        JWTAuthResponseDTO jwtAuthResponse = authService.login(loginDto);
        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }
}
