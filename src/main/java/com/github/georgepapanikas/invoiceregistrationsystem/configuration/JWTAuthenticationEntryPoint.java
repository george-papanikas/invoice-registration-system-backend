package com.github.georgepapanikas.invoiceregistrationsystem.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Entry point for handling authentication errors in JWT-based security.
 *
 * <p>Invoked when an unauthenticated or invalid JWT is used to access a protected endpoint.
 * Sends a 401 Unauthorized response with an appropriate error message.</p>
 */
@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commences an authentication scheme.
     *
     * <p>Sends an HTTP 401 (Unauthorized) error to the client with the exception's message
     * when authentication fails or is missing.</p>
     *
     * @param request       the {@link HttpServletRequest} during which the authException occurred
     * @param response      the {@link HttpServletResponse} to which the error will be sent
     * @param authException the {@link AuthenticationException} that triggered this entry point
     * @throws IOException      if an input or output error occurs
     * @throws ServletException if a general servlet exception occurs
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
