package org.springframework.samples.petclinic.owners.api;

public record OwnerView(
    Integer id,
    String firstName,
    String secondName,
    String email
) {
}
