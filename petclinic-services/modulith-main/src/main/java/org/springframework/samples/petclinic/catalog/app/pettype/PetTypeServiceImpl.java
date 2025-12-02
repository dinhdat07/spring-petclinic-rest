package org.springframework.samples.petclinic.catalog.app.pettype;

import java.util.Collection;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Cacheable(value = "petTypes", key = "#id")
    public Optional<PetType> findById(int id) throws DataAccessException {
        return petTypeRepository.findById(id);
    }

    @Override
    @Cacheable(value = "petTypes_all", key = "'all'")
    public Collection<PetType> findAll() throws DataAccessException {
        return petTypeRepository.findAll();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "petTypes", key = "#petType.id"),
            @CacheEvict(value = "petTypes", key = "'all'")
    })
    @Transactional
    public void save(PetType petType) throws DataAccessException {
        petTypeRepository.save(petType);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "petTypes", key = "#petType.id"),
            @CacheEvict(value = "petTypes", key = "'all'")
    })
    public void delete(PetType petType) throws DataAccessException {
        petTypeRepository.delete(petType);
    }
}
