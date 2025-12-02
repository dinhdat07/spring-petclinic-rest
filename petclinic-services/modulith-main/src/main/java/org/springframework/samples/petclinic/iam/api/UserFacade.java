package org.springframework.samples.petclinic.iam.api;

import java.util.Optional;

public interface UserFacade {

    Optional<UserAuthView> findByUsername(String username);
}

