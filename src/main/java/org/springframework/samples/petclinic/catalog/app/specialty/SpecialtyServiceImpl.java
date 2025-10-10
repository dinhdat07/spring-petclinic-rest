package org.springframework.samples.petclinic.catalog.app.specialty;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.catalog.domain.Specialty;
import org.springframework.samples.petclinic.catalog.infra.jpa.SpecialtyJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyJpaRepository specialtyRepository;

    public SpecialtyServiceImpl(SpecialtyJpaRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public Optional<Specialty> findById(int id) throws DataAccessException {
        return specialtyRepository.findById(id);
    }

    @Override
    public Collection<Specialty> findAll() throws DataAccessException {
        return specialtyRepository.findAll();
    }

    @Override
    @Transactional
    public void save(Specialty specialty) throws DataAccessException {
        specialtyRepository.save(specialty);
    }

    @Override
    @Transactional
    public void delete(Specialty specialty) throws DataAccessException {
        specialtyRepository.delete(specialty);
    }

    @Override
    public List<Specialty> findByNameIn(Set<String> names) throws DataAccessException {
        return specialtyRepository.findByNameIn(names);
    }
}

