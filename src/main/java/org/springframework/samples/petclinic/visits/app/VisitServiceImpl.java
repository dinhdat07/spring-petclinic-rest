package org.springframework.samples.petclinic.visits.app;

import java.util.Collection;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.visits.domain.Visit;
import org.springframework.samples.petclinic.visits.infra.jpa.VisitJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VisitServiceImpl implements VisitService {

    private final VisitJpaRepository visitRepository;

    public VisitServiceImpl(VisitJpaRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Override
    public Optional<Visit> findById(int visitId) throws DataAccessException {
        return visitRepository.findById(visitId);
    }

    @Override
    public Page<Visit> findAll(Pageable pageable) throws DataAccessException {
        return visitRepository.findAll(pageable);
    }

    @Override
    public Page<Visit> findByPetId(Integer petId, Pageable pageable) {
        return visitRepository.findByPetId(petId, pageable);
    }

    @Override
    @Transactional
    public void save(Visit visit) throws DataAccessException {
        visitRepository.save(visit);
    }

    @Override
    @Transactional
    public void delete(Visit visit) throws DataAccessException {
        visitRepository.delete(visit);
    }
}

