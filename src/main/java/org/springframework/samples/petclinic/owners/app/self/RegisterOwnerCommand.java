package org.springframework.samples.petclinic.owners.app.self;

public record RegisterOwnerCommand(
    String username,
    String password,
    String firstName,
    String lastName,
    String address,
    String city,
    String telephone
) {
}

