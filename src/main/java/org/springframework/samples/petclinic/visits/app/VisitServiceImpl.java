package org.springframework.samples.petclinic.visits.app;

import java.util.Collection;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
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
    public Collection<Visit> findAll() throws DataAccessException {
        return visitRepository.findAll();
    }

    @Override
    public Collection<Visit> findByPetId(int petId) {
        return visitRepository.findByPetId(petId);
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

