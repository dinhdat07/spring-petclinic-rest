package org.springframework.samples.petclinic.catalog.app.specialty;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.catalog.domain.Specialty;

public interface SpecialtyService {

    Specialty findById(int id) throws DataAccessException;

    Collection<Specialty> findAll() throws DataAccessException;

    void save(Specialty specialty) throws DataAccessException;

    void delete(Specialty specialty) throws DataAccessException;

    List<Specialty> findByNameIn(Set<String> names) throws DataAccessException;
}
