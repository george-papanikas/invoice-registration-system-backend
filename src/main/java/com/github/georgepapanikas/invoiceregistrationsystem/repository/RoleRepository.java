package com.github.georgepapanikas.invoiceregistrationsystem.repository;

import com.github.georgepapanikas.invoiceregistrationsystem.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for performing CRUD operations on {@link Role} entities.
 * Extends Spring Data’s {@link JpaRepository} to inherit standard data access methods.
 *
 * <p>Additional query methods can be declared here and will be auto‑implemented
 * by Spring Data JPA based on method naming conventions.</p>
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Retrieves a {@link Role} entity by its name.
     *
     * @param name the name of the role to search for (e.g., "ROLE_ADMIN")
     * @return the matching Role, or null if none exists
     */
    Role findByName(String name);
}

