package org.springframework.samples.petclinic.appointments.api;

import java.util.Optional;

/**
 * Provides vet contact details to the appointments module without creating a direct dependency on the vets module.
 */
public interface VetContactPort {

    Optional<VetContact> findContact(Integer vetId);

    record VetContact(String name, String email) { }
}
