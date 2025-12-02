package org.springframework.samples.petclinic.vets.app;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.samples.petclinic.appointments.api.VetContactPort;
import org.springframework.samples.petclinic.vets.api.VetsFacade;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class VetContactAdapter implements VetContactPort {

    private final VetsFacade vetsFacade;

    @Override
    public Optional<VetContact> findContact(Integer vetId) {
        return vetsFacade.findById(vetId)
            .map(vet -> new VetContact(buildName(vet.firstName(), vet.lastName()), vet.email()));
    }

    private String buildName(String firstName, String lastName) {
        return Stream.of(firstName, lastName)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .reduce((a, b) -> a + " " + b)
            .orElse(null);
    }
}
