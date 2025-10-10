package org.springframework.samples.petclinic.catalog.api;

/**
 * Read model for pet type information exposed outside the catalog module.
 */
public record PetTypeView(Integer id, String name) {
}

