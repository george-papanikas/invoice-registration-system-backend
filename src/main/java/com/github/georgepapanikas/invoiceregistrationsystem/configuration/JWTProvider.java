package com.github.georgepapanikas.invoiceregistrationsystem.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * Component responsible for creating, parsing, and validating JSON Web Tokens (JWT)
 * used in the application’s authentication flow.
 */
@Component
public class JWTProvider {

    /**
     * Base64‑encoded secret key used to sign and verify JWTs.
     * Loaded from the application property {@code app.jwt-secret}.
     */
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    /**
     * Expiration time for generated tokens, in milliseconds.
     * Loaded from the application property {@code app.jwt-expiration-milliseconds}.
     */
    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    /**
     * Generates a signed JWT containing the authenticated user’s identifier as the subject.
     *
     * @param authentication the authentication token containing user details
     * @return a signed JWT string, valid for the configured expiration period
     */
    public String generateToken(Authentication authentication) {

        String username = authentication.getName(); //user can log in with username or email
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(getSecretKey())
                .compact();
        return token;
    }

    /**
     * Decodes the configured Base64 secret and constructs an HMAC‑SHA key for signing.
     *
     * @return the {@link Key} used for signing and verifying JWTs
     */
    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Parses the given JWT and extracts the subject, which represents the username.
     *
     * @param token the JWT string to parse
     * @return the username (subject) contained in the token
     * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        String username = claims.getSubject();
        return username;
    }

    /**
     * Validates the given JWT by verifying its signature and expiration.
     *
     * @param token the JWT string to validate
     * @return {@code true} if the token is well‑formed, signed correctly, and not expired
     * @throws io.jsonwebtoken.JwtException if the token fails validation
     */
    public boolean validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token);
        return true;
    }
}
