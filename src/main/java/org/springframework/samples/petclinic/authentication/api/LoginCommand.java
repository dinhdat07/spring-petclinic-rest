package org.springframework.samples.petclinic.authentication.api;

import jakarta.validation.constraints.NotBlank;

@org.springframework.modulith.NamedInterface
public record LoginCommand(
    @NotBlank String username,
    @NotBlank String password
) {}

