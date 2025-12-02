package org.springframework.samples.petclinic.visits.infra.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.visits.domain.Visit;

public interface VisitJpaRepository extends JpaRepository<Visit, Integer> {

    List<Visit> findByPetId(Integer petId);
}

