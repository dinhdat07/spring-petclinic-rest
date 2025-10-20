package org.springframework.samples.petclinic.visits.app;

import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.visits.domain.Visit;

public interface VisitService {

    Optional<Visit> findById(int visitId) throws DataAccessException;

    Page<Visit> findAll(Pageable pageable) throws DataAccessException;

    Page<Visit> findByPetId(Integer petId, Pageable pageable);

    void save(Visit visit) throws DataAccessException;

    void delete(Visit visit) throws DataAccessException;
}
