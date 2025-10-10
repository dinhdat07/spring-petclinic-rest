package org.springframework.samples.petclinic.visits.app;

import java.util.Collection;
import java.util.Optional;

import org.springframework.samples.petclinic.visits.api.VisitCreateCommand;
import org.springframework.samples.petclinic.visits.api.VisitUpdateCommand;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.samples.petclinic.visits.domain.Visit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VisitsFacadeImpl implements VisitsFacade {

    private final VisitService visitService;

    public VisitsFacadeImpl(VisitService visitService) {
        this.visitService = visitService;
    }

    @Override
    @Transactional
    public VisitView createVisit(VisitCreateCommand command) {
        Visit visit = new Visit();
        visit.setPetId(command.petId());
        visit.setDescription(command.description());
        if (command.date() != null) {
            visit.setDate(command.date());
        }
        visitService.save(visit);
        return toView(visit);
    }

    @Override
    public Optional<VisitView> findById(int visitId) {
        return visitService.findById(visitId).map(this::toView);
    }

    @Override
    public Collection<VisitView> findByPetId(int petId) {
        return visitService.findByPetId(petId).stream()
            .map(this::toView)
            .toList();
    }

    @Override
    public Collection<VisitView> findAll() {
        return visitService.findAll().stream()
            .map(this::toView)
            .toList();
    }

    @Override
    @Transactional
    public Optional<VisitView> updateVisit(int visitId, VisitUpdateCommand command) {
        return visitService.findById(visitId)
            .map(existing -> {
                if (command.date() != null) {
                    existing.setDate(command.date());
                }
                existing.setDescription(command.description());
                visitService.save(existing);
                return toView(existing);
            });
    }

    @Override
    @Transactional
    public boolean deleteVisit(int visitId) {
        return visitService.findById(visitId)
            .map(existing -> {
                visitService.delete(existing);
                return true;
            })
            .orElse(false);
    }

    private VisitView toView(Visit visit) {
        return new VisitView(
            visit.getId(),
            visit.getPetId(),
            visit.getDate(),
            visit.getDescription()
        );
    }
}

