package org.springframework.samples.petclinic.catalog.infra.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.catalog.domain.PetType;

public interface PetTypeJpaRepository extends JpaRepository<PetType, Integer> {

    Optional<PetType> findByNameIgnoreCase(String name);
}

