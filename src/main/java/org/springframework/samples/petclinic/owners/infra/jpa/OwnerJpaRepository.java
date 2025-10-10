package org.springframework.samples.petclinic.owners.infra.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.owners.domain.Owner;

public interface OwnerJpaRepository extends JpaRepository<Owner, Integer> {

    List<Owner> findByLastNameStartingWithIgnoreCase(String lastName);

    @EntityGraph(attributePaths = "pets")
    Optional<Owner> findWithPetsById(Integer id);
}

