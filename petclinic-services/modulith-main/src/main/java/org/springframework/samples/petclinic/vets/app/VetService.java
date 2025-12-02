package org.springframework.samples.petclinic.vets.app;

import java.util.Collection;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.vets.domain.Vet;

public interface VetService {

    Optional<Vet> findById(int id) throws DataAccessException;

    Collection<Vet> findAll() throws DataAccessException;

    Optional<Vet> findByUsername(String username) throws DataAccessException;

    void save(Vet vet) throws DataAccessException;

    void delete(Vet vet) throws DataAccessException;
}

