package org.springframework.samples.petclinic.owners.app.pet;

import java.util.Collection;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.catalog.api.PetTypesFacade;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.owners.infra.jpa.PetJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PetServiceImpl implements PetService {

    private final PetJpaRepository petRepository;
    private final PetTypesFacade petTypesFacade;

    public PetServiceImpl(PetJpaRepository petRepository, PetTypesFacade petTypesFacade) {
        this.petRepository = petRepository;
        this.petTypesFacade = petTypesFacade;
    }

    @Override
    public Optional<Pet> findById(int id) throws DataAccessException {
        return petRepository.findById(id);
    }

    @Override
    public Collection<Pet> findAll() throws DataAccessException {
        return petRepository.findAll();
    }

    @Override
    @Transactional
    public void save(Pet pet) throws DataAccessException {
        if (pet.getTypeId() == null) {
            throw new IllegalArgumentException("Pet type id must be provided");
        }
        petTypesFacade.findById(pet.getTypeId())
            .orElseThrow(() -> new IllegalArgumentException("Unknown pet type id: " + pet.getTypeId()));
        petRepository.save(pet);
    }

    @Override
    @Transactional
    public void delete(Pet pet) throws DataAccessException {
        petRepository.delete(pet);
    }
}


