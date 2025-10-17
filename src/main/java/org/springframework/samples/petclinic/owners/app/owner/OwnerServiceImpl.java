package org.springframework.samples.petclinic.owners.app.owner;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.infra.jpa.OwnerJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OwnerServiceImpl implements OwnerService {

    private final OwnerJpaRepository ownerRepository;

    public OwnerServiceImpl(OwnerJpaRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    @Cacheable(value = "owners", key = "#id")
    public Optional<Owner> findById(int id) throws DataAccessException {
        return ownerRepository.findWithPetsById(id).or(() -> ownerRepository.findById(id));
    }

    @Override
    @Cacheable(value = "owners", key = "'all'", sync = true)
    public Collection<Owner> findAll() throws DataAccessException {
        return ownerRepository.findAll();
    }

    @Override
    @Cacheable(value = "owners", key = "'lastName: ' + #lastName")
    public Collection<Owner> findByLastName(String lastName) throws DataAccessException {
        List<Owner> owners = ownerRepository.findByLastNameStartingWithIgnoreCase(lastName);
        return owners;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "owners", key = "#owner.id"),
            @CacheEvict(value = "owners", key = "'all'"),
            @CacheEvict(value = "owners", key = "'lastName: ' + #lastName")
    })
    @Transactional
    public void save(Owner owner) throws DataAccessException {
        ownerRepository.save(owner);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "owners", key = "#owner.id"),
            @CacheEvict(value = "owners", key = "'all'"),
            @CacheEvict(value = "owners", key = "'lastName: ' + #lastName")
    })
    @Transactional
    public void delete(Owner owner) throws DataAccessException {
        ownerRepository.delete(owner);
    }
}
