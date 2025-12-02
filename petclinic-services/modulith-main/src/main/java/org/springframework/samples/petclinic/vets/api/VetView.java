package org.springframework.samples.petclinic.vets.api;

public record VetView(
    Integer id,
    String firstName,
    String lastName,
    String email
) {
}
