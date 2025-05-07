package com.github.georgepapanikas.invoiceregistrationsystem.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Central security configuration class.
 * <p>
 * Configures method-level security, HTTP security rules (CSRF, CORS, endpoint authorization),
 * JWT authentication filter, exception handling, and exposes core security beans.
 * </p>
 */
@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    /**
     * Service for loading user-specific data during authentication.
     */
    private UserDetailsService userDetailsService;

    /**
     * Filter that processes and validates JWT tokens on incoming requests.
     */
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Entry point for handling JWT authentication errors (e.g., invalid or missing JWT).
     */
    private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Defines the {@link PasswordEncoder} bean used to hash and verify user passwords.
     *
     * @return a {@link BCryptPasswordEncoder} instance for secure password encoding
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain, including:
     * <ul>
     *   <li>Disabling CSRF protection</li>
     *   <li>Enabling CORS with default settings</li>
     *   <li>Permitting unauthenticated access to authentication endpoints and HTTP OPTIONS (CORS preflight)</li>
     *   <li>Permitting unauthenticated access to swagger/openapi endpoints:</li>
     *   <li>Requiring authentication for all other requests</li>
     *   <li>Registering the JWT authentication filter before username/password filter</li>
     *   <li>Customizing exception handling via {@link JWTAuthenticationEntryPoint}</li>
     * </ul>
     *
     * @param http the primary {@link HttpSecurity} builder to configure HTTP security
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if there is an error building the security filter chain
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .authorizeHttpRequests((authorizeRequests) -> {
                    authorizeRequests.requestMatchers(
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/swagger-resources/**",
                                    "/webjars/**")
                            .permitAll();
                    authorizeRequests.requestMatchers("/api/auth/**").permitAll();
                    authorizeRequests.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    authorizeRequests.anyRequest().authenticated();
                }).httpBasic(withDefaults());
        // Handling of unauthorized access attempts
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint));
        // Addition of JWT token filter before standard authentication
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Exposes the {@link AuthenticationManager} bean that is used by Spring Security
     * for processing authentication requests.
     *
     * @param configuration the {@link AuthenticationConfiguration} provided by Spring
     * @return the configured {@link AuthenticationManager}
     * @throws Exception if unable to retrieve the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}