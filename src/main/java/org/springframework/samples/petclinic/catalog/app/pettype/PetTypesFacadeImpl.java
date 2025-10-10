package org.springframework.samples.petclinic.catalog.app.pettype;

import java.util.Collection;
import java.util.Optional;

import org.springframework.samples.petclinic.catalog.api.PetTypeView;
import org.springframework.samples.petclinic.catalog.api.PetTypesFacade;
import org.springframework.samples.petclinic.catalog.domain.PetType;
import org.springframework.samples.petclinic.catalog.infra.jpa.PetTypeJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PetTypesFacadeImpl implements PetTypesFacade {

    private final PetTypeJpaRepository petTypeRepository;

    public PetTypesFacadeImpl(PetTypeJpaRepository petTypeRepository) {
        this.petTypeRepository = petTypeRepository;
    }

    @Override
    public Optional<PetTypeView> findById(int id) {
        return petTypeRepository.findById(id).map(this::toView);
    }

    @Override
    public Collection<PetTypeView> findAll() {
        return petTypeRepository.findAll().stream()
            .map(this::toView)
            .toList();
    }

    private PetTypeView toView(PetType petType) {
        return new PetTypeView(petType.getId(), petType.getName());
    }
}

