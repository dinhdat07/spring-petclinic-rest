package org.springframework.samples.petclinic.owners.app.pet;

import java.util.Collection;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owners.domain.Pet;

public interface PetService {

    Optional<Pet> findById(int id) throws DataAccessException;

    Collection<Pet> findAll() throws DataAccessException;

    void save(Pet pet) throws DataAccessException;

    void delete(Pet pet) throws DataAccessException;
}
