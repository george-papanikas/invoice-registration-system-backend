package com.github.georgepapanikas.invoiceregistrationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing the JWT authentication response returned upon successful login.
 * <p>
 * Contains the access token, token type, and user role.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JWTAuthResponseDTO {

    /**
     * The JWT access token.
     */
    private String accessToken;

    /**
     * The type of the token, e.g., "Bearer".
     */
    private String tokenType = "Bearer";

    /**
     * The role granted to the authenticated user.
     */
    private String role;
}
