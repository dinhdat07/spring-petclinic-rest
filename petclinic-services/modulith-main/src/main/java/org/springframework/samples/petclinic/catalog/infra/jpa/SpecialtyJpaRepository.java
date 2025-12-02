package org.springframework.samples.petclinic.catalog.infra.jpa;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.catalog.domain.Specialty;

public interface SpecialtyJpaRepository extends JpaRepository<Specialty, Integer> {

    Optional<Specialty> findByNameIgnoreCase(String name);

    List<Specialty> findByNameIn(Set<String> names);
}

