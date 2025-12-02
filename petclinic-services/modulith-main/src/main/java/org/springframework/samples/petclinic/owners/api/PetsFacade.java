package org.springframework.samples.petclinic.owners.api;

import java.util.Optional;

public interface PetsFacade {

    Optional<PetView> findById(int id);

}
