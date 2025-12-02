package org.springframework.samples.petclinic.vets.app;

import java.util.Optional;

import org.springframework.samples.petclinic.vets.api.VetView;
import org.springframework.samples.petclinic.vets.api.VetsFacade;
import org.springframework.samples.petclinic.vets.domain.Vet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VetsFacadeImpl implements VetsFacade {

    private final VetService vetService;

    @Override
    public Optional<VetView> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return vetService.findById(id).map(this::toView);
    }

    private VetView toView(Vet vet) {
        return new VetView(vet.getId(), vet.getFirstName(), vet.getLastName(), vet.getEmail());
    }
}
