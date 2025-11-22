package org.springframework.samples.petclinic.vets.api;

import java.util.Optional;

public interface VetsFacade {

    Optional<VetView> findById(Integer id);
}
