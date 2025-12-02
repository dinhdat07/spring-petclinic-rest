package org.springframework.samples.petclinic.authentication.api;

import jakarta.validation.Valid;

@org.springframework.modulith.NamedInterface
public interface AuthenticationFacade {

    TokenView authenticate(@Valid LoginCommand command);
}

