package org.springframework.samples.petclinic.common;

/**
 * Minimal contract for entities that expose a primary key.
 */
public interface Identifiable {

    Integer getId();

    default boolean isNew() {
        return getId() == null;
    }
}

