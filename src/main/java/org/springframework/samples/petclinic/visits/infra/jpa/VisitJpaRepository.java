package org.springframework.samples.petclinic.visits.infra.jpa;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.visits.domain.Visit;

public interface VisitJpaRepository extends JpaRepository<Visit, Integer> {

    Page<Visit> findByPetId(Integer petId, Pageable pageable);
}

