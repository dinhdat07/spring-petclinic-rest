package org.springframework.samples.petclinic.authentication.web;

public record RegisterOwnerResponse(
    Integer ownerId,
    String username
) {
}

