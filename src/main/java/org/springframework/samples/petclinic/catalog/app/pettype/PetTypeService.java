package org.springframework.samples.petclinic.catalog.app.pettype;

import java.util.Collection;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.catalog.domain.PetType;

public interface PetTypeService {

    Optional<PetType> findById(int id) throws DataAccessException;

    Collection<PetType> findAll() throws DataAccessException;

    void save(PetType petType) throws DataAccessException;

    void delete(PetType petType) throws DataAccessException;
}
