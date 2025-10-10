package org.springframework.samples.petclinic.catalog.api;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Public entrypoint for specialty access from other modules.
 */
public interface SpecialtiesFacade {

    Optional<SpecialtyView> findById(int id);

    Collection<SpecialtyView> findAll();

    List<SpecialtyView> findByNames(Set<String> names);

    List<SpecialtyView> findByIds(Set<Integer> ids);
}

