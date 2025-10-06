package org.springframework.samples.petclinic.owners.app.owner;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owners.domain.Owner;

public interface OwnerService {

    Owner findById(int id) throws DataAccessException;

    Collection<Owner> findAll() throws DataAccessException;

    Collection<Owner> findByLastName(String lastName) throws DataAccessException;

    void save(Owner owner) throws DataAccessException;

    void delete(Owner owner) throws DataAccessException;
}
