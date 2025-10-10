package org.springframework.samples.petclinic.vets.app;

import java.util.Collection;
import java.util.Optional;

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
    public Optional<Vet> findById(int id) throws DataAccessException {
        return vetRepository.findById(id);
    }

    @Override
    public Collection<Vet> findAll() throws DataAccessException {
        return vetRepository.findAll();
    }

    @Override
    @Transactional
    public void save(Vet vet) throws DataAccessException {
        vetRepository.save(vet);
    }

    @Override
    @Transactional
    public void delete(Vet vet) throws DataAccessException {
        vetRepository.delete(vet);
    }
}

