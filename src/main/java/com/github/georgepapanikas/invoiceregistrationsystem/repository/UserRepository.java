package com.github.georgepapanikas.invoiceregistrationsystem.repository;

import com.github.georgepapanikas.invoiceregistrationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link User} entities.
 * Extends Spring Data’s {@link JpaRepository} to inherit standard data access methods.
 *
 * <p>Additional query methods can be declared here and will be auto‑implemented
 * by Spring Data JPA based on method naming conventions.</p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a {@link User} by their unique username.
     *
     * @param username the username to search for (must be non‑null)
     * @return an {@link Optional} containing the User if found, or empty if none exists
     */
    Optional<User> findByUsername(String username);

    /**
     * Retrieves a {@link User} by either their username or email.
     *
     * @param username the username to search for
     * @param email the email address to search for
     * @return an {@link Optional} containing the User if a match is found by username or email, otherwise empty
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Checks whether a {@link User} exists with the given email.
     *
     * @param email the email address to check for existence
     * @return {@code true} if a User with the given email exists, {@code false} otherwise
     */
    Boolean existsByEmail(String email);

    /**
     * Checks whether a {@link User} exists with the given username.
     *
     * @param username the username to check for existence
     * @return {@code true} if a User with the given username exists, {@code false} otherwise
     */
    Boolean existsByUsername(String username);
}
