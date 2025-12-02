package org.springframework.samples.petclinic.visits.app;

import java.util.Collection;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Cacheable(value = "visits", key = "'visitId: ' + #visitId")
    public Optional<Visit> findById(int visitId) throws DataAccessException {
        return visitRepository.findById(visitId);
    }

    @Override
    @Cacheable(value = "visits_all", key = "'all'")
    public Collection<Visit> findAll() throws DataAccessException {
        return visitRepository.findAll();
    }

    @Override
    @Cacheable(value = "visits_all", key = "'petId: ' + #petId")
    public Collection<Visit> findByPetId(int petId) {
        return visitRepository.findByPetId(petId);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "visits", key = "'visitId: ' + #visit.id"),
            @CacheEvict(value = "visits", key = "'all'"),
            @CacheEvict(value = "visits", key = "'petId: '+ #visit.petId")
    })
    public void save(Visit visit) throws DataAccessException {
        visitRepository.save(visit);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "visits", key = "'visitId: ' + #visit.id"),
            @CacheEvict(value = "visits", key = "'all'"),
            @CacheEvict(value = "visits", key = "'petId: '+ #visit.petId")
    })
    public void delete(Visit visit) throws DataAccessException {
        visitRepository.delete(visit);
    }
}
