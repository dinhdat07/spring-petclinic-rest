package org.springframework.samples.petclinic.visits.api;

import java.util.Collection;
import java.util.Optional;

/**
 * Public API for the visits module exposed to other application modules.
 */
public interface VisitsFacade {

    VisitView createVisit(VisitCreateCommand command);

    Optional<VisitView> findById(int visitId);

    Collection<VisitView> findByPetId(int petId);

    Collection<VisitView> findAll();

    Optional<VisitView> updateVisit(int visitId, VisitUpdateCommand command);

    boolean deleteVisit(int visitId);
}

