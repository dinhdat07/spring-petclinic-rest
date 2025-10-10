package org.springframework.samples.petclinic.catalog.api;

/**
 * Read model representation of a vet specialty.
 */
public record SpecialtyView(Integer id, String name) {
}

