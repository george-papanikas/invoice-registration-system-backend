package com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions;

/**
 * Exception thrown when an entity of the specified type and identifier is not found.
 *
 * <p>Use this exception to signal that a lookup by class and ID failed
 * to locate the expected record in the persistence layer.</p>
 */
public class EntityNotFoundException extends Exception {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new EntityNotFoundException for the given entity class and identifier.
     *
     * @param entityClass the class of the entity that was not found
     * @param id the identifier of the missing entity
     */
    public EntityNotFoundException(Class<?> entityClass, Long id) {
        super("Entity " + entityClass.getSimpleName() + " with id " + id + " not found");
    }
}
