package org.springframework.samples.petclinic.vets.app;

import java.util.Collection;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.vets.domain.Vet;
import org.springframework.samples.petclinic.vets.infra.jpa.VetJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VetServiceImpl implements VetService {

    private final VetJpaRepository vetRepository;

    public VetServiceImpl(VetJpaRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @Override
    @Cacheable(value = "vets", key = "#id")
    public Optional<Vet> findById(int id) throws DataAccessException {
        return vetRepository.findById(id);
    }

    @Override
    @Cacheable(value = "vets", key = "'all'")
    public Collection<Vet> findAll() throws DataAccessException {
        return vetRepository.findAll();
    }

    @Override
    @Cacheable(value = "vets", key = "'username: ' + #username", unless = "#result == null")
    public Optional<Vet> findByUsername(String username) throws DataAccessException {
        return vetRepository.findByUsernameIgnoreCase(username);
    }

    @Caching(evict = {
            @CacheEvict(value = "vets", key = "#vet.id"),
            @CacheEvict(value = "vets", key = "'username: ' + #vet.username"),
            @CacheEvict(value = "vets", key = "'all'")
    })
    @Transactional
    public void save(Vet vet) throws DataAccessException {
        vetRepository.save(vet);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "vets", key = "#vet.id"),
            @CacheEvict(value = "vets", key = "'username: ' + #vet.username"),
            @CacheEvict(value = "vets", key = "'all'")
    })
    @Transactional
    public void delete(Vet vet) throws DataAccessException {
        vetRepository.delete(vet);
    }
}
