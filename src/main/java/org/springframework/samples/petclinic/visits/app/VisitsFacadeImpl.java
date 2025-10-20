package org.springframework.samples.petclinic.visits.app;

import java.util.Collection;
import java.util.Optional;

import org.springframework.samples.petclinic.visits.api.VisitCreateCommand;
import org.springframework.samples.petclinic.visits.api.VisitUpdateCommand;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.samples.petclinic.visits.domain.Visit;
import org.springframework.samples.petclinic.visits.mapper.VisitMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VisitsFacadeImpl implements VisitsFacade {

    private final VisitService visitService;
    private final VisitMapper visitMapper;

    @Override
    public Optional<VisitView> findById(int visitId) {
        return visitService.findById(visitId).map(visitMapper::toView);
    }

    @Override
    public Collection<VisitView> findByPetId(Integer petId) {
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


}

