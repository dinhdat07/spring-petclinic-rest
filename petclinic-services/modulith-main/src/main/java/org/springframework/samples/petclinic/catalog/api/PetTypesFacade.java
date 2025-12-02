package org.springframework.samples.petclinic.catalog.api;

import java.util.Collection;
import java.util.Optional;

/**
 * Public entrypoint for pet type related operations available to other modules.
 */
public interface PetTypesFacade {

    Optional<PetTypeView> findById(int id);

    Collection<PetTypeView> findAll();
}

