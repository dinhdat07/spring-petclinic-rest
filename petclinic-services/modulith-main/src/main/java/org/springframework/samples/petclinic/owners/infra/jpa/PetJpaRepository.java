package org.springframework.samples.petclinic.owners.infra.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.owners.domain.Pet;

public interface PetJpaRepository extends JpaRepository<Pet, Integer> {

    Optional<Pet> findByNameIgnoreCaseAndOwnerId(String name, Integer ownerId);
}

