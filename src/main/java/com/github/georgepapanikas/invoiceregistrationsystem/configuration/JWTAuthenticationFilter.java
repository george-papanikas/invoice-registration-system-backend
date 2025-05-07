package com.github.georgepapanikas.invoiceregistrationsystem.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Servlet filter that intercepts each HTTP request to validate a JWT token from the
 * Authorization header and, if valid, set the authenticated {@link UsernamePasswordAuthenticationToken}
 * in the {@link SecurityContextHolder}.
 */
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    /**
     * Constructs a new JWTAuthenticationFilter.
     *
     * @param jwtProvider        provider for generating and validating JWT tokens
     * @param userDetailsService service to load user details by username
     */
    public JWTAuthenticationFilter(JWTProvider jwtProvider, UserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filters incoming requests, extracting a Bearer token from the Authorization header,
     * validating it, and setting the authentication in the security context if successful.
     * {@inheritDoc}
     *
     * @param request     the servlet request
     * @param response    the servlet response
     * @param filterChain the filter chain to delegate to the next filter
     * @throws ServletException if an error occurs during filtering
     * @throws IOException      if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            String username = jwtProvider.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header, if present and prefixed with "Bearer ".
     *
     * @param request the HTTP request containing the headers
     * @return the JWT token string without the "Bearer " prefix, or {@code null} if not found
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
