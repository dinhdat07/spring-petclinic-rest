package org.springframework.samples.petclinic.visits.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface VisitsFacade {
  Optional<VisitView> findById(int visitId);
  Page<VisitView> findByPetId(int petId, Pageable pageable);
  Page<VisitView> findAll(Pageable pageable);
}
