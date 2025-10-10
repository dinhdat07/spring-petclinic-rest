package org.springframework.samples.petclinic.catalog.app.pettype;

import java.util.Collection;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.catalog.domain.PetType;
import org.springframework.samples.petclinic.catalog.infra.jpa.PetTypeJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PetTypeServiceImpl implements PetTypeService {

    private final PetTypeJpaRepository petTypeRepository;

    public PetTypeServiceImpl(PetTypeJpaRepository petTypeRepository) {
        this.petTypeRepository = petTypeRepository;
    }

    @Override
    public Optional<PetType> findById(int id) throws DataAccessException {
        return petTypeRepository.findById(id);
    }

    @Override
    public Collection<PetType> findAll() throws DataAccessException {
        return petTypeRepository.findAll();
    }


    @Override
    @Transactional
    public void save(PetType petType) throws DataAccessException {
        petTypeRepository.save(petType);
    }

    @Override
    @Transactional
    public void delete(PetType petType) throws DataAccessException {
        petTypeRepository.delete(petType);
    }
}

