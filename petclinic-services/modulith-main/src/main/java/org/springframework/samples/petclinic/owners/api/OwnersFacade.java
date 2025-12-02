package org.springframework.samples.petclinic.owners.api;

import java.util.Optional;

public interface OwnersFacade {
    Optional<OwnerView> findById(Integer id);
    
}
