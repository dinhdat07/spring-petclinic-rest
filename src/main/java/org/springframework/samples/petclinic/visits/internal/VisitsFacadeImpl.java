package org.springframework.samples.petclinic.visits.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.visits.api.VisitCommand;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.samples.petclinic.visits.domain.Visit;
import org.springframework.samples.petclinic.visits.infra.jpa.VisitJpaRepository; // đổi theo tên repo của bạn
import org.springframework.samples.petclinic.visits.mapper.VisitMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class VisitsFacadeImpl implements VisitsFacade {

  private final VisitJpaRepository repo;
  private final VisitMapper visitMapper;

  public VisitsFacadeImpl(VisitJpaRepository repo, VisitMapper visitMapper) {
    this.repo = repo;
    this.visitMapper = visitMapper;
  }

  @Override
  @Transactional
  public VisitView create(@Valid VisitCommand.Create cmd) {
    var v = new Visit();
    v.setPetId(cmd.petId());
    v.setDescription(cmd.description());
    if (cmd.date() != null) v.setDate(cmd.date());
    return visitMapper.toView(repo.save(v));
  }

  @Override
  public Optional<VisitView> findById(int visitId) {
    return repo.findById(visitId).map(visitMapper::toView);
  }

  @Override
  public Page<VisitView> findByPetId(int petId, Pageable pageable) {
    return repo.findByPetId(petId, pageable).map(visitMapper::toView);
  }

  @Override
  public Page<VisitView> findAll(Pageable pageable) {
    return repo.findAll(pageable).map(visitMapper::toView);
  }

  @Override
  @Transactional
  public Optional<VisitView> update(int visitId, @Valid VisitCommand.Update cmd) {
    return repo.findById(visitId).map(existing -> {
      existing.setDescription(cmd.description());
      if (cmd.date() != null) existing.setDate(cmd.date());
      return visitMapper.toView(repo.save(existing));
    });
  }

  @Override
  @Transactional
  public boolean delete(int visitId) {
    return repo.findById(visitId).map(v -> {
                repo.delete(v); return true; 
              }).orElse(false);
  }
}
