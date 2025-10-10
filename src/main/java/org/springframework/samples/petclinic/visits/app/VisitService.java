package org.springframework.samples.petclinic.visits.app;

import java.util.Collection;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.visits.domain.Visit;

public interface VisitService {

    Optional<Visit> findById(int visitId) throws DataAccessException;

    Collection<Visit> findAll() throws DataAccessException;

    Collection<Visit> findByPetId(int petId);

    void save(Visit visit) throws DataAccessException;

    void delete(Visit visit) throws DataAccessException;
}
