package org.springframework.samples.petclinic.owners.app.pet;

import java.util.Optional;

import org.springframework.samples.petclinic.owners.api.PetView;
import org.springframework.samples.petclinic.owners.api.PetsFacade;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.owners.infra.jpa.PetJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PetsFacadeImpl implements PetsFacade {
    
    private final PetJpaRepository petRepository;

    @Override
    public Optional<PetView> findById(int id) {
        return petRepository.findById(id).map(this::toView);
    }

    private PetView toView(Pet pet) {
        Owner owner = pet.getOwner();
        Integer ownerId = owner == null ? null : owner.getId();
        return new PetView(pet.getId(), pet.getName(), ownerId, pet.getTypeId());
    }
    
}
