package org.springframework.samples.petclinic.vets.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.vets.domain.Vet;

public interface VetJpaRepository extends JpaRepository<Vet, Integer> {
}

