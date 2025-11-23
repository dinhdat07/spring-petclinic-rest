package org.springframework.samples.petclinic.owners.app.owner;

import java.util.Optional;

import org.springframework.samples.petclinic.owners.api.OwnerView;
import org.springframework.samples.petclinic.owners.api.OwnersFacade;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.infra.jpa.OwnerJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class OwnersFacadeImpl implements OwnersFacade {
    private final OwnerJpaRepository ownerRepository;

    @Override 
    public Optional<OwnerView> findById(Integer id) {
        return ownerRepository.findById(id).map(this::toView);
    }

    private OwnerView toView(Owner owner) {
        return new OwnerView(owner.getId(), owner.getFirstName(), owner.getLastName(), owner.getEmail());
    }
    
}
