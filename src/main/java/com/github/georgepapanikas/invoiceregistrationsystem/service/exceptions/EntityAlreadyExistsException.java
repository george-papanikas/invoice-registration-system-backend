package com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions;

/**
 * Exception thrown when attempting to create or insert an entity
 * that already exists in the system.
 */
public class EntityAlreadyExistsException extends Exception {

    /**
     * Unique identifier for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new EntityAlreadyExistsException for the specified entity class.
     *
     * @param entityClass the class of the entity that already exists
     */
    public EntityAlreadyExistsException(Class<?> entityClass) {
        super("Entity " + entityClass.getSimpleName() + " already exists");
    }
}
